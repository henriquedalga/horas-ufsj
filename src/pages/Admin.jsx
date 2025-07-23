import { Fragment, useEffect, useState } from "react";

import Header from "../components/Header";
import Modal from "../components/Modal";
import ModalAdmin from "../components/ModalAdmin";
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
  const [editingAdmin, setEditingAdmin] = useState(null);
  const [isModalAdminOpen, setIsModalAdminOpen] = useState(false);

  const [sortConfig, setSortConfig] = useState({
    key: "nome", // Chave do objeto para ordenar (padrão: 'nome')
    direction: "ascending", // 'ascending' ou 'descending'
  });

  const handleOpenEditModal = async () => {
    // Garante que só executa se houver um item selecionado
    if (!selectedModId) return;

    try {
      // Busca os dados mais recentes do admin selecionado
      const adminData = await AdminService.getAdminById(selectedModId);

      // Coloca os dados no estado, o que fará o modal abrir com as informações
      setEditingAdmin(adminData);
    } catch (error) {
      console.error("Erro ao buscar dados do admin para edição:", error);
      alert("Não foi possível carregar os dados para edição.");
    }
  };

  const handleCloseEditModal = () => {
    setEditingAdmin(null);
  };

  const handleSort = (key) => {
    let direction = "ascending";
    // Se já estiver ordenando por esta chave, inverte a direção
    if (sortConfig.key === key && sortConfig.direction === "ascending") {
      direction = "descending";
    }
    setSortConfig({ key, direction });
  };

  const sortedItemsMod = [...itemsMod].sort((a, b) => {
    // LÓGICA ESPECIAL PARA A COLUNA BOOLEANA 'MODERADOR'
    if (sortConfig.key === "role") {
      const aIsMod = a.role === "mod"; // Será true ou false
      const bIsMod = b.role === "mod"; // Será true ou false

      // O JavaScript trata true como 1 e false como 0 em operações matemáticas.
      if (sortConfig.direction === "ascending") {
        return aIsMod - bIsMod; // Ordena false (0) antes de true (1)
      } else {
        return bIsMod - aIsMod; // Ordena true (1) antes de false (0) -> Marcados primeiro
      }
    }

    // LÓGICA ORIGINAL PARA COLUNAS DE TEXTO (NOME, EMAIL)
    // Certifique-se que as chaves existem nos objetos 'a' e 'b' antes de comparar
    if (a[sortConfig.key] < b[sortConfig.key]) {
      return sortConfig.direction === "ascending" ? -1 : 1;
    }
    if (a[sortConfig.key] > b[sortConfig.key]) {
      return sortConfig.direction === "ascending" ? 1 : -1;
    }
    return 0;
  });

  // Move handleModSelectionChange here so it's accessible in JSX
  const handleModSelectionChange = (itemId) => {
    // Usamos um operador ternário para a lógica:
    // Se o ID clicado JÁ É o que está selecionado, desmarque-o (setando para null).
    // Senão, selecione o novo ID.
    setSelectedModId((prevSelectedId) =>
      prevSelectedId === itemId ? null : itemId
    );
  };

  const handleOpenAdminModal = () => {
    setIsModalAdminOpen(true);
  };

  const handleCloseAdminModal = () => {
    setIsModalAdminOpen(false);
  };

  const refetchModData = async () => {
    try {
      const admins = await AdminService.getAdmins();
      setItemsMod(admins);
    } catch (err) {
      console.error("Erro ao re-buscar admins:", err);
    }
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
                  .sort((a, b) => {
                    // Função auxiliar para calcular a prioridade de um item
                    const getPriority = (item) => {
                      if (item.pendente === true) {
                        return 1; // Prioridade 1: Pendentes vêm primeiro.
                      }
                      if (item.status === "ABERTO") {
                        return 2; // Prioridade 2: Abertos vêm em segundo.
                      }
                      if (item.status === "FECHADO") {
                        return 3; // Prioridade 3: Fechados vêm por último.
                      }
                      return 99; // Padrão para qualquer outro caso.
                    };

                    const priorityA = getPriority(a);
                    const priorityB = getPriority(b);

                    return priorityA - priorityB;
                  })
                  .map((item) => {
                    // 1. Crie um mapa para associar o status a uma classe de cor.
                    const statusColorMap = {
                      true: "success",
                      false: "",
                    };

                    // 2. Pegue a classe correta do mapa. Usa '' como padrão se o status não for encontrado.
                    const tagColorClass = statusColorMap[item.pendente] || "";

                    return (
                      <Fragment key={item.id}>
                        {/* É melhor usar um ID único como key */}
                        <button
                          className="br-item"
                          onClick={() => openModalExtensao(item.id)}
                          disabled={
                            item.status === "APROVADO" ||
                            item.status === "REPROVADO"
                          }
                        >
                          <div className="row min-h-8 align-items-center">
                            <div className="col">{item.nome}</div>
                            {/* 3. Adicione a classe de cor dinâmica à sua tag */}
                            <div
                              className={`col-auto br-tag relative group ${tagColorClass}`}
                            >
                              {item.status}
                            </div>
                          </div>
                        </button>
                        <span className="br-divider"></span>
                      </Fragment>
                    );
                  })}
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
                  // CORREÇÃO DO BUG: Usando a variável de busca correta para esta lista
                  .filter((item) =>
                    item.nome
                      .toLowerCase()
                      .includes(searchComplementar.toLowerCase())
                  )
                  .filter((item) =>
                    statusFiltroComplementar === "TODOS"
                      ? true
                      : item.status === statusFiltroComplementar
                  )
                  .sort((a, b) => {
                    // 2. Defina a ordem de prioridade dos status.
                    const statusOrder = {
                      ABERTO: 1, // Prioridade máxima
                      FECHADO: 2, // Segunda prioridade
                    };

                    // 3. Compare os itens 'a' e 'b' com base na ordem definida.
                    const orderA = statusOrder[a.status] || 99; // Usa 99 para status desconhecidos
                    const orderB = statusOrder[b.status] || 99;

                    return orderA - orderB;
                  })
                  .map((item) => {
                    // 1. O mesmo mapa de status para classes de cor
                    const statusClassMap = {
                      ABERTO: "success",
                      FECHADO: "",
                    };

                    // 2. A mesma lógica para pegar a classe correta
                    const tagColorClass = statusClassMap[item.status] || "";

                    return (
                      // Usando o ID do item na key, que é a melhor prática
                      <Fragment key={item.id}>
                        <button
                          className="br-item"
                          onClick={() => openModalComplementar(item.id)}
                          disabled={
                            item.status === "APROVADO" ||
                            item.status === "REPROVADO"
                          }
                        >
                          <div className="row align-items-center min-h-8">
                            <div className="col">{item.nome}</div>
                            {/* 3. Aplicação da classe de cor dinâmica na tag */}
                            <div
                              className={`col-auto br-tag relative group ${tagColorClass}`}
                            >
                              {item.status}
                            </div>
                          </div>
                        </button>
                        <span className="br-divider"></span>
                      </Fragment>
                    );
                  })}
              </div>
            </div>
            {user.role === "mod" && (
              <div className="tab-panel" id="panel-3">
                <div className="pt-3">
                  <div className="pl-6 pr-6">
                    <div className="flex flex-col md:flex-row pt-2 pb-4 w-full align-items-center justify-between ">
                      <button
                        className="br-button primary"
                        type="button"
                        aria-label="Adicionar Funcionário"
                        onClick={handleOpenAdminModal}
                      >
                        <i className="fas fa-plus pr-2" aria-hidden="true"></i>
                        Adicionar
                      </button>
                      <button
                        className="br-button secondary ms-2"
                        type="button"
                        aria-label="Remover Funcionário"
                        disabled={!selectedModId}
                      >
                        <i className="fas fa-minus pr-2" aria-hidden="true"></i>
                        Remover
                      </button>
                      <button
                        className="br-button secondary ms-2"
                        type="button"
                        aria-label="Editar Funcionário"
                        disabled={!selectedModId}
                        onClick={handleOpenEditModal}
                      >
                        <i className="fas fa-edit pr-2" aria-hidden="true"></i>
                        Editar
                      </button>
                    </div>
                  </div>

                  <div className="grid grid-cols-[2fr_3fr_auto] items-center px-4 bg-blue-100">
                    <div className="contents font-bold text-gray-600">
                      <button
                        type="button"
                        className="text-start py-2"
                        onClick={() => handleSort("nome")}
                      >
                        Nome
                        {sortConfig.key === "nome" && (
                          <i
                            className={`fas ${
                              sortConfig.direction === "ascending"
                                ? "fa-sort-up"
                                : "fa-sort-down"
                            } ml-1`}
                          ></i>
                        )}
                      </button>
                      {/* Adicionamos 'text-center' para o cabeçalho do Email */}
                      <button
                        type="button"
                        className="text-center py-2 mr-6 pr-6"
                        onClick={() => handleSort("email")}
                      >
                        Email
                        {sortConfig.key === "email" && (
                          <i
                            className={`fas ${
                              sortConfig.direction === "ascending"
                                ? "fa-sort-up"
                                : "fa-sort-down"
                            } ml-1`}
                          ></i>
                        )}
                      </button>
                      {/* Adicionamos 'text-end' para o cabeçalho do Moderador */}
                      <button
                        type="button"
                        className="text-end py-2"
                        onClick={() => handleSort("role")}
                      >
                        Moderador
                        {sortConfig.key === "role" && (
                          <i
                            className={`fas ${
                              sortConfig.direction === "ascending"
                                ? "fa-sort-up"
                                : "fa-sort-down"
                            } ml-1`}
                          ></i>
                        )}
                      </button>
                    </div>
                  </div>

                  <span className="br-divider"></span>
                  {sortedItemsMod.map((item, index) => (
                    <Fragment key={index}>
                      <button
                        key={index}
                        className={`br-item ${
                          selectedModId === item.id ? "selected" : ""
                        }`}
                        onClick={() => handleModSelectionChange(item.id)}
                      >
                        <div className="grid grid-cols-[2fr_3fr_auto] items-center px-2">
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
      {editingAdmin && (
        <ModalAdmin
          // Passa os dados do admin para o modal preencher o formulário
          adminToEdit={editingAdmin}
          onClose={handleCloseEditModal}
          onSuccess={() => {
            refetchModData();
            handleCloseEditModal();
          }}
        />
      )}
      {isModalAdminOpen && (
        <ModalAdmin
          // Passe a função de fechar como prop para o modal
          onClose={handleCloseAdminModal}
          // Ver passo bônus abaixo
          onSuccess={() => {
            refetchModData();
            handleCloseAdminModal(); // Fecha o modal após o sucesso
          }}
        />
      )}
      <Modal
        isOpen={modalInfo.isOpen}
        onClose={() => setModalInfo({ ...modalInfo, isOpen: false })}
        nome={modalInfo.nome}
        arquivos={modalInfo.arquivos}
      />
    </>
  );
}
