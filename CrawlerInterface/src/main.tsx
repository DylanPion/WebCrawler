import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import FileList from "./components/FileList.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <FileList />
  </StrictMode>
);
