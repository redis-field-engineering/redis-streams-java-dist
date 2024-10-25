import {useState, useEffect} from "react";
import {Button, Table} from "react-bootstrap";
import '../styles/tailwind.css'

function TrackedHashtags(props){
    const[trendingTopics, setTrendingTopics] = useState({});
    const[stateUpdater, setStateUpdater] = useState(0);

    useEffect(() => {

        const startup = async() => {
            try{
                const response = await fetch("/api/tags");
                const data = await response.json();
                setTrendingTopics(data);
            } catch (error){
                console.log(error);
            }
        }

        startup();



    }, [stateUpdater, props.refresh]);

    const untrack = async(hashtag) =>{
        try{
            await fetch(`/api/tags?tag=${hashtag}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        } catch(error){
            console.log(error);
        }

        setStateUpdater(stateUpdater+1)
    }


    return(
        <div className="rounded-2xl left-div text-sm bg-redis-pencil-700 text-redis-pencil-200 font-bold overflow-y-scroll no-scrollbar mr-2">
            <label className="justify-center bg-redis-yellow-500 text-redis-pencil-950 mb-4 flex text-2xl font-bold">Tracked Hashtags</label>
            <Table className="ml-2 mb-2">
                <thead>
                    <tr>
                        <th className="text-left">Topic</th>
                        <th className="text-left">Count</th>
                        <th/>
                    </tr>
                </thead>
                <tbody>
                    {
                        Object.entries(trendingTopics).map(
                            ([tag, value]) => {
                                return <tr key={tag}>
                                    <td className="max-w-xs truncate py-1">{tag}</td>
                                    <td>{value}</td>
                                    <td>
                                        <button className="mt-2 bg-redis-pen-800 rounded"
                                                onClick={() => props.handleSelect(tag, false)}><label
                                            className="p-2 mt-1 mb-1 text-light-gray">Monitor</label>
                                        </button>
                                    </td>
                                    <td>
                                        <button className="mt-2 bg-redis-red-500 rounded ml-2"
                                                onClick={() => untrack(tag)}><label
                                            className="p-2 mt-1 mb-1 text-light-gray">Untrack</label>
                                        </button>
                                    </td>
                                </tr>
                            }
                        )
                    }
                </tbody>
            </Table>

        </div>
    )
}

export default TrackedHashtags;