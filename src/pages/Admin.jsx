import { Fragment, useEffect, useState } from "react";

import Header from "../components/Header";
import Modal from "../components/Modal";
import AdminService from "../services/admin.service";
import storageService from "../services/storage.service";

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
  const [user, setUser] = useState({});
  const [itemsMod, setItemsMod] = useState([]);
  const [selectedModId, setSelectedModId] = useState(null);

  // Move handleModSelectionChange here so it's accessible in JSX
  const handleModSelectionChange = (itemId) => {
    // Usamos um operador ternário para a lógica:
    // Se o ID clicado JÁ É o que está selecionado, desmarque-o (setando para null).
    // Senão, selecione o novo ID.
    setSelectedModId((prevSelectedId) =>
      prevSelectedId === itemId ? null : itemId
    );
  };

  useEffect(() => {
    const currentUser = storageService.getUserData();
    const token = storageService.getAuthToken();

    // A verificação correta é simplesmente checar se o token existe.
    if (!token) {
      console.log("Nenhum token encontrado, redirecionando...");
      window.location.href = "/main";
      return; // Para a execução do useEffect aqui
    }
    setUser(currentUser);

    if (window.core?.BRTab) {
      const abasList = [];
      for (const brTab of window.document.querySelectorAll(".br-tab")) {
        abasList.push(new window.core.BRTab("br-tab", brTab));
      }
    }

    const fetchData = async () => {
      try {
        const extensao = await AdminService.getExtensao();
        console.log("Alunos:", extensao);
        setItemsExtensao(extensao);
        const complementar = await AdminService.getComplementar();
        setItemsComplementar(complementar);
      } catch (err) {
        console.error("Erro ao buscar alunos:", err);
      }
      if (currentUser.role === "mod") {
        try {
          const admins = await AdminService.getAdmins();
          setItemsMod(admins);
          console.log("Admins:", admins);
        } catch (err) {
          console.error("Erro ao buscar admins:", err);
        }
      }
    };

    fetchData();
  }, []);

  useEffect(() => {
    // Este efeito vai rodar sempre que as listas de itens ou o usuário mudarem.
    // Isso garante que a biblioteca seja re-inicializada se a estrutura do DOM mudar.
    if (window.core?.BRItem && window.core?.BRTab) {
      console.log("Sincronizando bibliotecas de UI (BRTab, BRItem)...");

      const abasList = [];
      for (const brTab of window.document.querySelectorAll(".br-tab")) {
        abasList.push(new window.core.BRTab("br-tab", brTab));
      }

      const itemList = [];
      for (const brItem of window.document.querySelectorAll(".br-item")) {
        itemList.push(new window.core.BRItem("br-item", brItem));
      }
    }
  }, [user, itemsExtensao, itemsComplementar, itemsMod]);

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
      <div className="w-11/12 md:container flex flex-col items-center mx-auto pt-6">
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
              {user.role === "mod" && (
                <li className="tab-item">
                  <button type="button" data-panel="panel-3">
                    <span className="name">
                      <span className="d-flex flex-column flex-sm-row">
                        <span className="name">Moderação</span>
                      </span>
                    </span>
                  </button>
                </li>
              )}
            </ul>
          </nav>
          <div className="tab-content">
            <div className="tab-panel active" id="panel-1">
              <div className="pt-4">
                <div className="br-input has-icon pb-2">
                  <input
                    id="searchbox-1"
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
                  .map((item, index) => (
                    <Fragment key={index}>
                      <button
                        className="br-item"
                        onClick={() => openModalExtensao(item.id)}
                        key={index}
                        disabled={
                          item.status === "APROVADO" ||
                          item.status === "REPROVADO"
                        }
                      >
                        <div class="row  min-h-8 align-items-center">
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
                    id="searchbox-2"
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
                  .map((item, index) => (
                    <Fragment key={index}>
                      <button
                        className="br-item"
                        onClick={() => openModalComplementar(item.id)}
                        key={index}
                        disabled={
                          item.status === "APROVADO" ||
                          item.status === "REPROVADO"
                        }
                      >
                        <div class="row align-items-center min-h-8">
                          <div class="col">{item.nome}</div>
                          <div class="col-auto">{item.status}</div>
                        </div>
                      </button>
                      <span className="br-divider"></span>
                    </Fragment>
                  ))}
              </div>
            </div>
            {user.role === "mod" && (
              <div className="tab-panel" id="panel-3">
                <div className="pt-4">
                  <div className="br-input has-icon pb-2">
                    <input
                      id="searchbox-3"
                      type="text"
                      placeholder="Quem você procura?"
                      value={searchExtensao}
                      // onChange={(e) => setSearchExtensao(e.target.value)}
                    />
                    <button
                      className="br-button circle small"
                      type="button"
                      aria-label="Pesquisar"
                    >
                      <i className="fas fa-search" aria-hidden="true"></i>
                    </button>
                  </div>
                  <div className="pl-6 pr-6">
                    <div className="flex flex-col md:flex-row pt-2 pb-4 w-full align-items-center justify-between ">
                      <button
                        className="br-button primary"
                        type="button"
                        aria-label="Adicionar Funcionário"
                      >
                        <i className="fas fa-plus" aria-hidden="true"></i>
                        Adicionar Funcionário
                      </button>
                      <button
                        className="br-button secondary ms-2"
                        type="button"
                        aria-label="Remover Funcionário"
                        disabled={!selectedModId}
                      >
                        <i className="fas fa-minus" aria-hidden="true"></i>
                        Remover Funcionário
                      </button>
                      <button
                        className="br-button secondary ms-2"
                        type="button"
                        aria-label="Editar Funcionário"
                        disabled={!selectedModId}
                      >
                        <i className="fas fa-edit" aria-hidden="true"></i>
                        Editar Funcionário
                      </button>
                    </div>
                  </div>
                  <span className="br-divider"></span>
                  {itemsMod.map((item, index) => (
                    <Fragment key={index}>
                      <button
                        key={index}
                        className={`br-item ${
                          selectedModId === item.id ? "selected" : ""
                        }`}
                        onClick={() => handleModSelectionChange(item.id)}
                      >
                        <div className="d-flex flex-row min-h-8 w-full align-items-center justify-between">
                          <span className="name">{item.nome}</span>
                          <span className="email align-self-center">
                            {item.email}
                          </span>
                          <div className="ms-auto">
                            <input
                              id={`check-${index}`}
                              name={`check-${index}`}
                              type="checkbox"
                              defaultChecked={item.role === "mod"}
                              className="br-checkbox"
                            />
                          </div>
                        </div>
                      </button>
                      <span className="br-divider"></span>
                    </Fragment>
                  ))}
                </div>
              </div>
            )}
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
