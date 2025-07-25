/* eslint-disable @typescript-eslint/no-unused-vars */
import { useEffect, useState } from 'react';
import './App.css'
import type { DscFile } from './DscFile';
import axios from 'axios';
import { saveAs } from 'file-saver';
import type { UploadStatus } from './uploadStatus';

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
  const [uploadStatus, setUploadStatus] = useState<UploadStatus[]>([]);

  const [contextMenu, setContextMenu] = useState<{
    x: number;
    y: number;
    visible: boolean;
    file: DscFile | null;
  }>({ x: 0, y: 0, visible: false, file: null });

  const [selectedFile, setSelectedFile] = useState<DscFile | null>(null);
  const [isDragActive, setIsDragActive] = useState(false);

  useEffect(() => {
    axios.get('http://127.0.0.1:8080/files/848921667833167933')
      .then(response => setData(response.data))
      .catch(error => console.error(error));
  }, []);

  const handleOnContextMenu = (e: React.MouseEvent, rightClickFile: DscFile) => {
    e.preventDefault();
    setContextMenu({
      x: e.clientX,
      y: e.clientY,
      visible: true,
      file: rightClickFile
    });
  }


  const handleCloseContextMenu = () => {
    setContextMenu((prev) => ({ ...prev, visible: false, file: null }));
  }


  const handleRename = (file: DscFile) => {
    const newName = prompt('Podaj nową nazwę pliku:', file.name);
    if (newName && newName !== file.name) {
      axios.put("http://localhost:8080/file/" + newName, file, {
          headers:{
            'Content-Type': 'application/json'
          }
      }).catch(err =>{
        console.error("Error while renaming file: " + err);
      })
    }
    handleCloseContextMenu();
  }


  const handleDownload = (file : DscFile) => {
    
    
      
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
      },
      onUploadProgress: (progressEvent) => {
        if (progressEvent.total) {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          setUploadStatus([...uploadStatus, {progress: percentCompleted, name: file.name}]);
        }
      }
    }).then(res => {
      const newData = [...data];
      newData.push(res.data);
      setData(newData);
      setUploadStatus(prev => prev.filter(item => item.name !== file.name));
    }).catch(err => {
      console.error("Error while sending file", err);
      setUploadStatus(prev => prev.filter(item => item.name !== file.name));
    });
    handleCloseContextMenu();
  }

  const handleDelete = (dscFile : DscFile) =>{
      axios.delete("http://localhost:8080/file", {
        data: dscFile,
        headers:{
          'Content-Type': 'application/json'
        }
      }).then(res =>{
        //Delete file from local array
        const newData = [...data].filter(item => item.id !== dscFile.id)
        setData(newData);
      }).catch(err=>{
        console.error(err);
      })
      handleCloseContextMenu();
  }

  const handleFileClick = (file: DscFile) => {
    setSelectedFile(file);
    handleCloseContextMenu();
  };

  const handleCloseSidebar = () => {
    setSelectedFile(null);
  };

  // Funkcje obsługi drag & drop
  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragActive(true);
  };

  const handleDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragActive(false);
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      const file = e.dataTransfer.files[0];
      handleUpload(file);
    }
  };

  return (
    <div className='app-wrapper'>
      <h1>storagium - In development</h1>

      <h2>Guild id: <span style={{color: 'grey'}}>{guildId}</span></h2>

      {uploadStatus.length > 0 && uploadStatus[0].progress > 0 && (
        <div style={{display: 'flex', flexDirection: 'column', gap: 8}}>
          {uploadStatus.map(item => (
            <div style={{ width: 200, margin: '16px auto' }}>
              <div style={{ width: `${item.progress}%`, background: 'green', height: 10 }} />
              <div>{item.name} {item.progress}%</div>
            </div>
          ))}
        </div>
      )}

      <div
        className={'container' + (selectedFile ? ' container--with-sidebar' : '') + (isDragActive ? ' container--drag-active' : '')}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
            {data.map(item => {
              return (
                <div key={item.id} className='file' onClick={() => handleFileClick(item)} onContextMenu={e => handleOnContextMenu(e, item)}>
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

      </div>

      {contextMenu.visible && contextMenu.file && (
        <div
          className='contextMenu'
          style={{ top: contextMenu.y, left: contextMenu.x, position: 'fixed', zIndex: 2000 }}
          onClick={e => e.stopPropagation()}
        >
          <>
            <button className='contextMenuBtn' onClick={() => handleRename(contextMenu.file!)}>Rename</button>
            <button className='contextMenuBtn' onClick={() => handleDownload(contextMenu.file!)}>Download</button>
            <button className='contextMenuBtn' onClick={() => handleDelete(contextMenu.file!)}>Delete</button>
          </>
        </div>
      )}

      {selectedFile && (
        <div className='sidebar-right'>
          <button style={{float: 'right', margin: 8, fontSize: 18, border: 'none', background: 'none', cursor: 'pointer'}} onClick={handleCloseSidebar}>×</button>
          <div style={{padding: '32px 24px 24px 24px'}}>
            <h2>File details</h2>
            <div style={{marginBottom: 16}}>
              {getFileExtension(selectedFile.name) == "png" || getFileExtension(selectedFile.name) == "jpg" ? <img style={{width: '100%'}} src={'http://localhost:8080/image/' + encodeURIComponent(JSON.stringify(selectedFile))}/> : getProperFileIcon(selectedFile.name)}
              
            </div>
            <div><b>Name:</b> {selectedFile.name}</div>
            <div><b>Size:</b> {selectedFile.size/1024 < 10240 ? (Math.round(selectedFile.size/1024) + " KB") : (Math.round(selectedFile.size/1024/1024) + " MB")}</div>
            <div><b>Guild ID:</b> {selectedFile.guildId}</div>
            <div><b>Upload date:</b> {new Date(selectedFile.uploadDate).toLocaleString()}</div>
            <div style={{marginTop: 24, display: 'flex', flexDirection: 'column', gap: 8}}>
              <button className='contextMenuBtn' onClick={() => handleDownload(selectedFile)}>Download</button>
              <button className='contextMenuBtn' onClick={() => handleRename(selectedFile)}>Rename</button>
              <button className='contextMenuBtn' onClick={() => handleDelete(selectedFile)}>Delete</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

const getProperFileIcon = (fileName : string) =>{
  const fileType = getFileExtension(fileName)

  if(fileTypesWithIcons.includes(fileType)){
    return <img className='smallIcon' src={`src/assets/${fileType}.png`} alt={fileType}/>
  }
  
  return <img className='smallIcon' src='src/assets/file.png'/>
}

const getFileExtension = (fileName: string) =>{
  const fileNameParts : string[] = fileName.split(".");
  return fileNameParts[fileNameParts.length - 1];
}

export default App
