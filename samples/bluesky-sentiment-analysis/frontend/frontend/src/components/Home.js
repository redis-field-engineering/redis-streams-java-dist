import TrackedHashtags from "./TrackedHashtags";
import React, {useState} from "react";
import {Button} from "react-bootstrap";
import TopicSentiments from "./TopicSentiments";
import TrackedKeywords from "./TrackedKeywords";
import '../styles/tailwind.css'
import TopicMonitor from "./TopicMonitor";

function Home(){
    const[keyword, setKeyword] = useState(()=>'');
    const [selectedTopic, setSelectedTopic] = useState('');
    const [stateUpdater, setStateUpdater] = useState(false);
    const [dashboardToRender, setDashboardToRender] = useState(null);

    const handleInputChange = (e) => {
        setKeyword(e.target.value);
    }

    const handleSelectComponent = async (topic, isKeyword) => {

        if(isKeyword){
            await fetch(`/api/keywords?keyword=${topic}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }
        else {
            await fetch(`/api/tags?tag=${topic}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        console.log('switching topics to: ', topic);
        setSelectedTopic(()=>topic)
        setDashboardToRender("sentiment");
        setStateUpdater(!stateUpdater);
    }

    const addMonitoringPanel = async () => {

        setDashboardToRender("topicMonitor");
    }

    return (
        <div className="bg-redis-pencil-900 text-redis-pencil-200 font-mono h-screen">
            {/*Header*/}
            <header className="flex justify-center font-bold text-4xl pt-4">
                <h1>Bluesky Sentiment Tracking</h1>
            </header>
            {/* Input box for keyword */}
            <div className="rounded" style={{margin: '20px', textAlign: 'center'}}>
                <input
                    type="text"
                    value={keyword}
                    onChange={handleInputChange}
                    className="rounded mt-2 mb-2 text-sm font-medium text-redis-pencil-950 bg-redis-pencil-200 border placeholder-redis-pencil-500"
                    placeholder="Enter keyword/hashtag to track"
                    style={{padding: '10px', width: '300px', marginRight: '10px'}}
                />
                <button className="bg-redis-yellow-500 text-redis-pencil-950 py-2 px-4 rounded-2xl"
                        onClick={() => handleSelectComponent(keyword, false)}>Track as Hashtag
                </button>
                <button className="bg-redis-skyblue-500 text-redis-pencil-950 py-2 px-4 rounded-2xl  ml-2"
                        onClick={() => handleSelectComponent(keyword, true)}>Track as Keyword
                </button>

                <button className="bg-redis-red-500 text-redis-pencil-950 py-2 px-4 rounded-2xl ml-2"
                        onClick={() => addMonitoringPanel()}>Monitor Streams
                </button>
            </div>

            <div style={{display: 'flex', height: '80vh', margin: '20px'}}>
                <TrackedHashtags handleSelect={handleSelectComponent} refresh={stateUpdater}/>
                <TrackedKeywords handleSelectComponent={handleSelectComponent} refresh={stateUpdater}/>

                <div style={{ flexGrow: 1, padding: '20px', overflowY: 'auto'}}>
                    {dashboardToRender ? (
                        <div>
                            {dashboardToRender === "sentiment" && selectedTopic ? (
                                <TopicSentiments name={selectedTopic} />
                            ) : (
                                <TopicMonitor />
                            )}
                        </div>

                    ) : (
                        <div className="font-bold text-4xl flex justify-center">Select a Hashtag / Keyword to display its sentiment</div>
                    )}
                </div>
            </div>


        </div>
    )
}

export default Home;