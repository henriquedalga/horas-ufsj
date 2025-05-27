import Header from "../components/Header";
import Upload from "../components/Upload";
import { useEffect } from "react";

export default function Student() {
  useEffect(() => {
    if (window.core?.BRTab) {
      const abasList = [];
      for (const brTab of window.document.querySelectorAll(".br-tab")) {
        abasList.push(new window.core.BRTab("br-tab", brTab));
      }
    }
  }, []);

  return (
    <>
      <Header />
      <div className="container flex flex-col items-center mx-auto pt-6">
        <div className="br-tab w-full max-w-lg">
          <nav className="tab-nav ">
            <ul>
              <li className="tab-item active">
                <button type="button" data-panel="panel-1">
                  <span className="name">
                    <span className="d-flex flex-column flex-sm-row">
                      <span className="name">Horas de Extensão</span>
                    </span>
                  </span>
                </button>
              </li>
              <li className="tab-item">
                <button type="button" data-panel="panel-2">
                  <span className="name">
                    <span className="d-flex flex-column flex-sm-row">
                      <span className="name">Horas Complementares</span>
                    </span>
                  </span>
                </button>
              </li>
            </ul>
          </nav>
          <div className="tab-content">
            <div className="tab-panel active" id="panel-1">
              <div className="pt-4">
                <Upload />
              </div>
            </div>
            <div className="tab-panel" id="panel-2">
              <div className="pt-4">
                <Upload />
              </div>
            </div>
          </div>
        </div>
        <div className="pt-2 flex w-full max-w-lg">
          <button className="br-button primary ml-auto" type="button">
            Enviar
          </button>
        </div>
      </div>
    </>
  );
}
