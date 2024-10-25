import logo from './logo.svg';
import './styles/styles.css'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import TrackedHashtags from "./components/TrackedHashtags";
import Home from "./components/Home";
import TopicSentiments from "./components/TopicSentiments";
import './styles/styles.css'

function App() {
  return (
      <Home/>
  );
}

export default App;
