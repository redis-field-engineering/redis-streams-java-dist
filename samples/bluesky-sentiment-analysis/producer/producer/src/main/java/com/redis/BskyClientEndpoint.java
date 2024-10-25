package com.redis;

import com.redis.Post;
import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.*;
import com.redis.streams.Producer;
import io.ipfs.cid.Cid;

import javax.websocket.*;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class BskyClientEndpoint {

    private final CountDownLatch latch;
    private final Producer producer;


    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to endpoint: " + session.getBasicRemote());
    }

    public static String bytesToHex(byte[] bytes){
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            // Convert each byte to its hexadecimal equivalent and pad with 0 if needed
            String hex = String.format("%02x", b);
            hexString.append(hex);
            hexString.append(",");
        }
        return hexString.toString();
    }

    java.util.Map<String, Object> cborMapToMap(Map map){
        java.util.Map<String, Object> result = new HashMap<>();
        map.getKeys().stream().forEach(key->result.put(key.toString(), map.get(key)));
        return result;
    }

    java.util.Map.Entry<Integer,Integer> readLEB128(byte[] bytes, int offset){
        int length = 0;
        int extraOffset;
        long shift = 0;
        for(extraOffset = 0; extraOffset < bytes.length; extraOffset++){
            int totalOffset = extraOffset + offset;
            byte b = bytes[totalOffset];

            // LEB128 stores 7 bits per byte, so we need to mask out the high bit
            length |= (long)(b & 0x7F) << shift;

            // If the MSB is not set, the number has ended
            if ((b & 0x80) == 0) {
                break;
            }

            // Otherwise, move to the next 7 bits
            shift += 7;
        }



        return new AbstractMap.SimpleEntry<>(length, extraOffset+1);
    }

    @OnMessage
    public void onMessage(byte[] message) {
        try {

            CborDecoder cborDecoder = new CborDecoder(new ByteArrayInputStream(message));
            List<DataItem> items = cborDecoder.decode();
            Map map = (Map) items.get(1);
            java.util.Map<String,Object> primitiveMap = new HashMap<>();
            map.getKeys().stream().forEach(key->primitiveMap.put(key.toString(), map.get(key)));


            if(!primitiveMap.containsKey("ops")){
                return;
            }
            Array ops = (Array) primitiveMap.get("ops");
            List<DataItem> dataItems = ops.getDataItems();
            if(dataItems.isEmpty()){
                return;
            }
            Map opMap = (Map) dataItems.get(0);
            java.util.Map<String, Object> primitiveOpMap = cborMapToMap(opMap);

            byte[] blocks = ((ByteString)primitiveMap.get("blocks")).getBytes();

            java.util.Map.Entry<Integer, Integer> headerLengthEntry = readLEB128(blocks, 0);

            int offset = headerLengthEntry.getValue();
            int headerLength = headerLengthEntry.getKey();

            if(!primitiveOpMap.get("path").toString().contains("post") || primitiveOpMap.get("path").toString().contains("repost")){
                return;
            }

            byte[] headerBytes = Arrays.copyOfRange(blocks, offset, offset + headerLength);

            offset += headerLength;
            java.util.Map<String, Object> postMap = new HashMap<>();
            while(offset < blocks.length){
                java.util.Map.Entry<Integer,Integer> nextDataLengthPair = readLEB128(blocks, offset);
                offset += nextDataLengthPair.getValue();
                int dataLength = nextDataLengthPair.getKey();
                final int CID_LENGTH = 46;
                byte[] cidBytes = Arrays.copyOfRange(blocks, offset, offset + CID_LENGTH);
                byte[] cidLengthByte = new byte[1];
                cidLengthByte[0] = cidBytes[3];
                int cidLength = readLEB128(cidLengthByte, 0).getKey() + 4;
                Cid cid = Cid.cast(cidBytes);

                if(cid.codec != Cid.Codec.DagCbor){
                    continue;
                }

                offset += cidLength;
                byte[] dataBytes = Arrays.copyOfRange(blocks, offset, offset + dataLength - cidLength);

                offset += dataLength - cidLength;

                CborDecoder dataDecoder = new CborDecoder(new ByteArrayInputStream(dataBytes));
                Map blockDataItems = (Map)dataDecoder.decode().get(0);

                java.util.Map<String, Object> blockDataMap = cborMapToMap(blockDataItems);

                if(blockDataMap.containsKey("text") || blockDataMap.containsKey("did")){
                    postMap.put("cid", cid.toString());
                    for(String key: blockDataMap.keySet()){
                        postMap.put(key, blockDataMap.get(key));
                    }
                }
            }

            Optional<Post> post = firehoseMessageToPost(postMap);

            if(post.isEmpty()){
                return;
            }

            producer.produce(post.get().toMap());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Optional<Post> firehoseMessageToPost(java.util.Map<String, Object> postMap){

        Array facets = (Array) postMap.get("facets");
        List<String> tags = new ArrayList<>();

        if(facets != null){
            for(DataItem facetItem: facets.getDataItems()){
                Map facetMap = (Map) facetItem;
                for(DataItem feature : ((Array)facetMap.get(new UnicodeString("features"))).getDataItems()){
                    Map asMap = (Map)feature;
                    String type = ((UnicodeString)asMap.get(new UnicodeString("$type"))).getString();
                    if(Objects.equals(type, "app.bsky.richtext.facet#tag")){
                        String tag = asMap.get(new UnicodeString("tag")).toString();
                        tags.add(tag);
                    }
                }
            }
        }

        Array langsArray = postMap.get("langs") == null ? null : (Array)postMap.get("langs");

        if(langsArray == null){
            return Optional.empty();
        }


        Optional<String> language = ((Array)postMap.get("langs")).getDataItems().stream().map(item->((UnicodeString)item).getString()).findFirst();

        if(language.isEmpty()){ // no language?
            return Optional.empty();
        }

        String text = ((UnicodeString)postMap.get("text")).getString();
        String did = ((UnicodeString)postMap.get("did")).getString();
        Instant createdAt = Instant.parse(((UnicodeString)postMap.get("createdAt")).getString());
        String languageStr = language.get();
        String cid = (String)postMap.get("cid");
        String[] hashTags = tags.toArray(new String[0]);

        return Optional.of(new Post(text,did,languageStr,hashTags,createdAt.toEpochMilli(), cid));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Connection closed: " + closeReason.getReasonPhrase());
        latch.countDown();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: " + throwable.getMessage());
    }

    public BskyClientEndpoint(URI uri, CountDownLatch latch, Producer producer){
        this.latch = latch;
        this.producer = producer;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try {
            container.connectToServer(this, uri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
