import React, { useEffect, useState } from "react";
import "../App.css";
import axios from "axios";

function FileList() {
  const [files, setFiles] = useState<string[]>([]);
  const [urlToCrawl, setUrlToCrawl] = useState<string>("");

  useEffect(() => {
    const fetchFiles = async () => {
      const res = await axios.get("/api/files");
      const data = await res.data;
      setFiles(data);
    };

    fetchFiles();

    const interval = setInterval(fetchFiles, 5000); // refresh every 5 seconds
    return () => clearInterval(interval); // cleanup
  }, []);

  const handleCrawl = async () => {
    const res = await fetch("http://localhost:3000/api/send-url", {
      method: "POST",
      body: JSON.stringify({ url: urlToCrawl }),
    });
  };

  const handleUrlChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUrlToCrawl(e.target.value);
  };

  const sendFileName = async (fileToDownload: string) => {
    try {
      const response = await axios.post(
        "/api/files/download",
        { fileName: fileToDownload },
        {
          responseType: "blob", // 👈 pour recevoir un blob binaire
        }
      );

      const blob = new Blob([response.data], {
        type: response.headers["content-type"],
      });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");

      // Nom du fichier pour le téléchargement
      const fileName = fileToDownload.split("/").pop() || "fichier.html";

      link.href = url;
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      link.remove();

      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Erreur lors du téléchargement :", error);
    }
  };

  return (
    <>
      <div className="file-list-container">
        <h1 className="file-list-title">Liste des fichiers HTML disponibles</h1>
        <ul className="file-list-ul">
          {files.map((file, idx) => (
            <li key={idx} onClick={() => sendFileName(file)}>
              {file.split("/").pop()}
            </li>
          ))}
        </ul>
      </div>
      <form
        className="crawl-form"
        onSubmit={(e) => {
          e.preventDefault();
          handleCrawl();
        }}
      >
        <input
          type="text"
          className="crawl-input"
          placeholder="URL to crawl"
          value={urlToCrawl}
          onChange={handleUrlChange}
        />
        <button type="submit" className="crawl-btn">
          Crawl
        </button>
      </form>
    </>
  );
}

export default FileList;
