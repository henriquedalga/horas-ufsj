import { useEffect, useState } from "react";

import Header from "../components/Header";
import Modal from "../components/Modal";
import { getAlunoById, getAlunos } from "../services/api";

export default function Admin() {
  const [modalInfo, setModalInfo] = useState({
    isOpen: false,
    nome: "",
    arquivos: [],
  });
  const [items, setItems] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFiltro, setStatusFiltro] = useState("TODOS");

  useEffect(() => {
    const user = JSON.parse(sessionStorage.getItem("user"));
    if (!user || !user.authToken) {
      window.location.href = "/main";
    }
    if (window.core?.BRTab) {
      const abasList = [];
      for (const brTab of window.document.querySelectorAll(".br-tab")) {
        abasList.push(new window.core.BRTab("br-tab", brTab));
      }
    }
    async function fetchAlunos() {
      try {
        const data = await getAlunos();
        console.log("Alunos:", data);
        setItems(data);
      } catch (err) {
        console.error("Erro ao buscar alunos:", err);
      }
    }

    fetchAlunos();
  }, []);

  useEffect(() => {
    if (window.core?.BRItem) {
      const itemList = [];
      const brItems = window.document.querySelectorAll(".br-item");
      for (const brItem of brItems) {
        itemList.push(new window.core.BRItem("br-item", brItem));
      }
    }
  }, [items]);

  const openModalWithData = async (id) => {
    try {
      const response = await getAlunoById(id);
      const arquivos = response.arquivos || [];
      const nome = response.nome || "Aluno sem nome";
      setModalInfo({
        isOpen: true,
        nome,
        arquivos,
      });
    } catch (error) {
      console.error("Erro ao buscar arquivos do aluno:", error);
    }
  };

  return (
    <>
      <Header />
      <div className="container flex flex-col items-center mx-auto pt-6">
        <div className="br-tab w-full max-w-4xl">
          <nav className="tab-nav ">
            <ul>
              <li className="tab-item active">
                <button type="button" data-panel="panel-1">
                  <span className="name">
                    <span className="d-flex flex-column flex-sm-row">
                      <span className="name">Extensão</span>
                    </span>
                  </span>
                </button>
              </li>
              <li className="tab-item">
                <button type="button" data-panel="panel-2">
                  <span className="name">
                    <span className="d-flex flex-column flex-sm-row">
                      <span className="name">Complementar</span>
                    </span>
                  </span>
                </button>
              </li>
            </ul>
          </nav>
          <div className="tab-content">
            <div className="tab-panel active" id="panel-1">
              <div className="pt-4">
                <div className="br-input has-icon pb-2">
                  <input
                    id="searchbox-26212"
                    type="text"
                    placeholder="O que você procura?"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                  <button
                    className="br-button circle small"
                    type="button"
                    aria-label="Pesquisar"
                  >
                    <i className="fas fa-search" aria-hidden="true"></i>
                  </button>
                </div>
                <div className="flex flex-row pb-4">
                  <div className="br-item" data-toggle="selection">
                    <div className="br-radio ">
                      <input
                        id="radio-01"
                        type="radio"
                        name="radio"
                        value="PENDENTE"
                        onChange={(e) => setStatusFiltro(e.target.value)}
                      />
                      <label for="radio-01">Abertos</label>
                    </div>
                  </div>
                  <div className="br-item" data-toggle="selection">
                    <div className="br-radio">
                      <input
                        id="radio-02"
                        type="radio"
                        name="radio"
                        value="APROVADO"
                        onChange={(e) => setStatusFiltro(e.target.value)}
                      />
                      <label for="radio-02">Concluídos</label>
                    </div>
                  </div>
                  <div className="br-item" data-toggle="selection">
                    <div className="br-radio">
                      <input
                        id="radio-03"
                        type="radio"
                        name="radio"
                        value="TODOS"
                        onChange={(e) => setStatusFiltro(e.target.value)}
                        defaultChecked
                      />
                      <label for="radio-03">Ambos</label>
                    </div>
                  </div>
                </div>
                <span className="br-divider"></span>

                {items
                  .filter((item) =>
                    item.nome.toLowerCase().includes(searchTerm.toLowerCase())
                  )
                  .filter((item) =>
                    statusFiltro === "TODOS"
                      ? true
                      : item.status === statusFiltro
                  )
                  .map((item) => (
                    <>
                      <button
                        className="br-item min-h-12"
                        onClick={() => openModalWithData(item.id)}
                        key={item.id}
                        disabled={
                          item.status === "APROVADO" ||
                          item.status === "REPROVADO"
                        }
                      >
                        {item.nome}
                      </button>
                      <span className="br-divider"></span>
                    </>
                  ))}
              </div>
            </div>
            <div className="tab-panel" id="panel-2">
              <div className="pt-4">/////////</div>
            </div>
          </div>
        </div>
      </div>
      <Modal
        isOpen={modalInfo.isOpen}
        onClose={() => setModalInfo({ ...modalInfo, isOpen: false })}
        nome={modalInfo.nome}
        arquivos={modalInfo.arquivos}
      />
    </>
  );
}
