import { Fragment, useEffect, useState } from "react";

import Header from "../components/Header";
import Modal from "../components/Modal";
import AdminService from "../services/admin.service";

export default function Admin() {
  const [modalInfo, setModalInfo] = useState({
    isOpen: false,
    nome: "",
    arquivos: [],
  });

  const [itemsExtensao, setItemsExtensao] = useState([]);
  const [itemsComplementar, setItemsComplementar] = useState([]);
  const [searchExtensao, setSearchExtensao] = useState("");
  const [searchComplementar, setSearchComplementar] = useState("");
  const [statusFiltroExtensao, setStatusFiltroExtensao] = useState("TODOS");
  const [statusFiltroComplementar, setStatusFiltroComplementar] =
    useState("TODOS");

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
        const extensao = await AdminService.getExtensao();
        console.log("Alunos:", extensao);
        setItemsExtensao(extensao);
        const complementar = await AdminService.getComplementar();
        setItemsComplementar(complementar);
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
  }, [itemsExtensao, itemsComplementar]);

  const openModalExtensao = async (id) => {
    try {
      const response = await AdminService.getExtensaoById(id);
      const nome = response?.nome || "";
      const arquivos = response?.arquivos || [];
      const tipo = "extensao";
      setModalInfo({
        isOpen: true,
        nome,
        id,
        arquivos,
        tipo,
      });
    } catch (error) {
      console.error("Erro ao buscar arquivos do aluno:", error);
    }
  };

  const openModalComplementar = async (id) => {
    try {
      const response = await AdminService.getComplementarById(id);
      const nome = response?.nome || "";
      const arquivos = response?.arquivos || [];
      const tipo = "complementar";
      setModalInfo({
        isOpen: true,
        nome,
        id,
        arquivos,
        tipo,
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
                    value={searchExtensao}
                    onChange={(e) => setSearchExtensao(e.target.value)}
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
                        name="radio-extensao"
                        value="PENDENTE"
                        onChange={(e) =>
                          setStatusFiltroExtensao(e.target.value)
                        }
                      />
                      <label for="radio-01">Abertos</label>
                    </div>
                  </div>
                  <div className="br-item" data-toggle="selection">
                    <div className="br-radio">
                      <input
                        id="radio-02"
                        type="radio"
                        name="radio-extensao"
                        value="APROVADO"
                        onChange={(e) =>
                          setStatusFiltroExtensao(e.target.value)
                        }
                      />
                      <label for="radio-02">Concluídos</label>
                    </div>
                  </div>
                  <div className="br-item" data-toggle="selection">
                    <div className="br-radio">
                      <input
                        id="radio-03"
                        type="radio"
                        name="radio-extensao"
                        value="TODOS"
                        onChange={(e) =>
                          setStatusFiltroExtensao(e.target.value)
                        }
                        defaultChecked
                      />
                      <label for="radio-03">Ambos</label>
                    </div>
                  </div>
                </div>
                <span className="br-divider"></span>

                {itemsExtensao
                  .filter((item) =>
                    item.nome
                      .toLowerCase()
                      .includes(searchExtensao.toLowerCase())
                  )
                  .filter((item) =>
                    statusFiltroExtensao === "TODOS"
                      ? true
                      : item.status === statusFiltroExtensao
                  )
                  .map((item) => (
                    <Fragment key={item.id}>
                      <button
                        className="br-item min-h-12 "
                        onClick={() => openModalExtensao(item.id)}
                        key={item.id}
                        disabled={
                          item.status === "APROVADO" ||
                          item.status === "REPROVADO"
                        }
                      >
                        <div class="row align-items-center">
                          <div class="col">{item.nome}</div>
                          <div class="col-auto">{item.status}</div>
                        </div>
                      </button>
                      <span className="br-divider"></span>
                    </Fragment>
                  ))}
              </div>
            </div>
            <div className="tab-panel" id="panel-2">
              <div className="pt-4">
                <div className="br-input has-icon pb-2">
                  <input
                    id="searchbox-Complementar"
                    type="text"
                    placeholder="O que você procura?"
                    value={searchComplementar}
                    onChange={(e) => setSearchComplementar(e.target.value)}
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
                        id="radio-04"
                        type="radio"
                        name="radio-complementar"
                        value="PENDENTE"
                        onChange={(e) =>
                          setStatusFiltroComplementar(e.target.value)
                        }
                      />
                      <label for="radio-04">Abertos</label>
                    </div>
                  </div>
                  <div className="br-item" data-toggle="selection">
                    <div className="br-radio">
                      <input
                        id="radio-05"
                        type="radio"
                        name="radio-complementar"
                        value="APROVADO"
                        onChange={(e) =>
                          setStatusFiltroComplementar(e.target.value)
                        }
                      />
                      <label for="radio-05">Concluídos</label>
                    </div>
                  </div>
                  <div className="br-item" data-toggle="selection">
                    <div className="br-radio">
                      <input
                        id="radio-06"
                        type="radio"
                        name="radio-complementar"
                        value="TODOS"
                        onChange={(e) =>
                          setStatusFiltroComplementar(e.target.value)
                        }
                        defaultChecked
                      />
                      <label for="radio-06">Ambos</label>
                    </div>
                  </div>
                </div>
                <span className="br-divider"></span>

                {itemsComplementar
                  .filter((item) =>
                    item.nome
                      .toLowerCase()
                      .includes(searchExtensao.toLowerCase())
                  )
                  .filter((item) =>
                    statusFiltroComplementar === "TODOS"
                      ? true
                      : item.status === statusFiltroComplementar
                  )
                  .map((item) => (
                    <Fragment key={item.id}>
                      <button
                        className="br-item min-h-12 "
                        onClick={() => openModalComplementar(item.id)}
                        key={item.id}
                        disabled={
                          item.status === "APROVADO" ||
                          item.status === "REPROVADO"
                        }
                      >
                        <div class="row align-items-center">
                          <div class="col">{item.nome}</div>
                          <div class="col-auto">{item.status}</div>
                        </div>
                      </button>
                      <span className="br-divider"></span>
                    </Fragment>
                  ))}
              </div>
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
