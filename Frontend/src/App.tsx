/* eslint-disable @typescript-eslint/no-unused-vars */
import { useEffect, useState } from 'react';
import './App.css'
import type { DscFile } from './DscFile';
import axios from 'axios';
import { saveAs } from 'file-saver';

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

  const [contextMenu, setContextMenu] = useState<{
    x: number;
    y: number;
    visible: boolean;
    file: DscFile | null;
  }>({ x: 0, y: 0, visible: false, file: null });

  useEffect(() => {
    axios.get('http://127.0.0.1:8080/files/848921667833167933')
      .then(response => setData(response.data))
      .catch(error => console.error(error));
  }, []);

  function handleOnContextMenu(e: React.MouseEvent, rightClickFile: DscFile) {
    e.preventDefault();
    setContextMenu({
      x: e.clientX,
      y: e.clientY,
      visible: true,
      file: rightClickFile
    });
  }


  function handleCloseContextMenu() {
    setContextMenu((prev) => ({ ...prev, visible: false, file: null }));
  }


  function handleRename(file: DscFile) {
    const newName = prompt('Podaj nową nazwę pliku:', file.name);
    if (newName && newName !== file.name) {
      alert(`Zmieniono nazwę pliku na: ${newName}`);
    }
    handleCloseContextMenu();
  }


  function handleDownload(file : DscFile) {
    
    
    if (contextMenu.file) {
      console.log(file);
      
      axios.post("http://localhost:8080/download", file, {
        responseType: 'blob',
        headers:{
          'Content-Type': 'application/json'
        }
      }).then(response =>{
        saveAs(response.data, file.name)
      }).catch(error =>{
        console.error("Error while download file", error);
      })
    }
    handleCloseContextMenu();
  }


  useEffect(() => {
    if (contextMenu.visible) {
      const listener = () => handleCloseContextMenu();
      window.addEventListener('click', listener);
      return () => window.removeEventListener('click', listener);
    }
  }, [contextMenu.visible]);

  const handleUpload = (file : File) =>{
      const formData = new FormData()
      formData.append("file", file)

      axios.post("http://localhost:8080/upload/" + guildId, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(res =>{
        console.log("File sent", res.data);
      }).catch(err =>{
        console.error("Error while sending file", err);
      })
  }

  return (
    <>
      <h1>storagium - In development</h1>

      <h2>Guild id: <span style={{color: 'grey'}}>{guildId}</span></h2>

      <div className='container'>
            {data.map(item => {
              return (
                <div className='file' onContextMenu={e => handleOnContextMenu(e, item)}>
                  <div className='fileHeader'>
                    {getProperFileIcon(item.name)}
                    <span className='fileName'>{item.name}</span>
                    <img
                      
                      className='dotsIcon'
                      src='src/assets/3dots.svg'
                      alt='więcej opcji'
                      onClick={e => {
                        e.stopPropagation();
                        handleOnContextMenu(
                          {
                            ...e,
                            preventDefault: () => {},
                            clientX: (e as React.MouseEvent).clientX,
                            clientY: (e as React.MouseEvent).clientY
                          } as React.MouseEvent,
                          item
                        );
                      }}
                    />
                  </div>
                </div>
              );
            })}

            <input type="file" onChange={e => {
                    const file = e.target.files?.[0];
                    if (file) handleUpload(file);
                  }} />
      </div>

      {contextMenu.visible && contextMenu.file && (
        <div
          className='contextMenu'
          style={{ top: contextMenu.y, left: contextMenu.x, position: 'fixed', zIndex: 2000 }}
          onClick={e => e.stopPropagation()}
        >
          <>
            <button className='contextMenuBtn' onClick={() => handleRename(contextMenu.file!)}>Change name</button>
            <button className='contextMenuBtn' onClick={() => handleDownload(contextMenu.file!)}>Download</button>
          </>
        </div>
      )}
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
