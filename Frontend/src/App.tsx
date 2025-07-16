/* eslint-disable @typescript-eslint/no-unused-vars */
import { useEffect, useState } from 'react';
import './App.css'
import type { DscFile } from './DscFile';
import axios from 'axios';

const fileTypesWithIcons: string[] = [
  "7z",
  "zip",
  "rar",
  "pdf",
  "doc",
  "docx",
  "xls",
  "xlsx",
  "png",
  "jpg",
  "jpeg",
  "gif",
  "txt",
  "mp3",
  "mp4",
  "ppt",
  "pptx"
];



function App() {

  const guildId : string = "848921667833167933";

  const [data, setData] = useState<DscFile[]>([])

  useEffect(() => {
    axios.get('http://127.0.0.1:8080/files/848921667833167933')
      .then(response => setData(response.data))
      .catch(error => console.error(error));
  }, []);


  return (
    <>
      <h1>storagium - In development</h1>

      <h2>Guild id: <span style={{color: 'grey'}}>{guildId}</span></h2>

      <div className='container'>
            {data.map(item => {
              return (
                <div className='file'>
                  <div className='fileHeader'>
                    {getProperFileIcon(item.name)}
                    <span className='fileName'>{item.name}</span>
                    <img
                      className='dotsIcon'
                      src='src/assets/3dots.svg'
                      alt='więcej opcji'
                    />
                  </div>
                </div>
              );
            })}
      </div>
      

      
    </>
  )
}

const getProperFileIcon = (fileName : string) =>{
  const fileNameParts : string[] = fileName.split(".");
  const fileType : string = fileNameParts[fileNameParts.length - 1];

  if(fileTypesWithIcons.includes(fileType)){
    return <img className='smallIcon' src={`src/assets/${fileType}.png`} alt={fileType}/>
  }
  
  return <img className='smallIcon' src='src/assets/file.png'/>
}

export default App
