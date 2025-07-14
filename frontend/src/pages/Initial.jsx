import { useEffect } from "react";

import Login from "../components/Login";

export default function Initial() {
  useEffect(() => {
    if (window.core?.BRTab) {
      const abasList = [];
      for (const brTab of window.document.querySelectorAll(".br-tab")) {
        abasList.push(new window.core.BRTab("br-tab", brTab));
      }
    }
  }, []);
  return (
    <div className="initial-wrapper min-h-screen w-screen flex items-center justify-center flex-col ">
      <div className="flex flex-row align-items-center ">
        <div className="col-auto ">
          <img
            src="/UFSJ.png"
            alt="logo"
            style={{ height: "100px", width: "auto" }}
          />
        </div>
        <div className="col-auto">
          <span
            className="br-divider vertical"
            style={{
              height: "100px",
              display: "inline-block",
              marginLeft: "8px",
              marginRight: "8px",
            }}
          ></span>
        </div>
        <div className="col-auto">
          <img
            src="/Ccomp.png"
            alt="logo"
            style={{ height: "100px", width: "auto" }}
          />
        </div>
      </div>

      <div className="br-tab pt-6">
        <nav className="tab-nav">
          <ul>
            <li className="tab-item active">
              <button type="button" data-panel="panel-1-icon">
                <span className="name">
                  <span className="d-flex flex-column flex-sm-row">
                    <span className="icon mb-1 mb-sm-0 mr-sm-1">
                      <i className="fas fa-image" aria-hidden="true"></i>
                    </span>
                    <span className="name">Aluno</span>
                  </span>
                </span>
              </button>
            </li>
            <li className="tab-item">
              <button type="button" data-panel="panel-2-icon">
                <span className="name">
                  <span className="d-flex flex-column flex-sm-row">
                    <span className="icon mb-1 mb-sm-0 mr-sm-1">
                      <i className="fas fa-image" aria-hidden="true"></i>
                    </span>
                    <span className="name">Admin</span>
                  </span>
                </span>
              </button>
            </li>
          </ul>
        </nav>
        <div className="tab-content">
          <div className="tab-panel active" id="panel-1-icon">
            <Login tipo="student" />
          </div>
          <div className="tab-panel" id="panel-2-icon">
            <Login tipo="admin" />
          </div>
        </div>
      </div>
    </div>
  );
}
