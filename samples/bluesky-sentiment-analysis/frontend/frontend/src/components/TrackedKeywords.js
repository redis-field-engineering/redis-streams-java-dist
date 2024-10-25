import {useEffect, useState} from "react";
import {Button, Table} from "react-bootstrap";

function TrackedKeywords(props){
    const[trackedKeywords, setTrackedKeywords] = useState([]);
    const[stateUpdater, setStateUpdater] = useState(0);
    useEffect(() => {
        const startup = async() =>{
            try{
                const response = await fetch('/api/keywords');
                const data = await response.json();
                setTrackedKeywords(data);
            } catch(error){
                console.log(error);
            }

        }


        startup();
    }, [stateUpdater, props.refresh])

    const untrack = async(keyword) => {
        try{
            await fetch(`/api/keywords?keyword=${keyword}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        } catch(error){
            console.log(error);
        }

        setStateUpdater(stateUpdater+1);
    }

    return(
        <div className="rounded-2xl left-div bg-redis-pencil-700 text-redis-pencil-200 font-bold text-sm overflow-y-scroll no-scrollbar">
            <label className="justify-center bg-redis-skyblue-500 text-redis-pencil-950 mb-4 flex text-2xl font-bold">Tracked Keywords</label>
            <Table className="ml-2">
                <thead>
                    <tr>
                        <th className="text-left">Keyword</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                {trackedKeywords && trackedKeywords.map((keyword) => {
                    return <tr className="mt-2" key={keyword}>
                        <td className="py-1 pr-2">{keyword}</td>
                        <td><Button className="rounded mt-2 bg-redis-pen-800 "
                                    onClick={() => props.handleSelectComponent(keyword, true)}><label
                            className="mt-1 mb-1 p-2">Monitor</label></Button></td>
                        <td><Button className="rounded mt-2 bg-redis-red-500 ml-2 "
                                    onClick={() => untrack(keyword)}><label
                            className="mt-1 mb-1 p-2">Untrack</label></Button></td>
                    </tr>
                })}
                </tbody>
            </Table>
        </div>
    )
}

export default TrackedKeywords;