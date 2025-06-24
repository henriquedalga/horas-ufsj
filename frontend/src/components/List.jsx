import { useState } from "react";

export default function List({ items, onItemClick }) {
  // const [selectedItem, setSelectedItem] = useState(null);

  // const handleClick = (item) => {
  //   setSelectedItem(item);
  //   onItemClick(item);
  // };

  return (
    <div class="br-list horizontal mt-5" role="list">

      <span class="br-divider"></span>
      <div class="group">
        <div
          class="br-item"
          role="listitem"
          data-toggle="collapse"
          data-target="h1"
        >
          <div class="content">
            <div class="flex-fill">Aluno 1</div>
            <i class="fas fa-angle-down" aria-hidden="true"></i>
          </div>
        </div>
        <div class="br-list" id="h1" role="list" hidden="hidden">
          <div class="br-item" role="listitem">
            <div class="row align-items-center">
              <div class="col-auto">
                <i class="fas fa-heartbeat" aria-hidden="true"></i>
              </div>
              <div class="col">Aluno 1</div>
            </div>
          </div>
          <span class="br-divider"></span>
          <div class="br-item" role="listitem">
            <div class="row align-items-center">
              <div class="col-auto">
                <i class="fas fa-heartbeat" aria-hidden="true"></i>
              </div>
              <div class="col">Aluno 2</div>
            </div>
          </div>
          <span class="br-divider"></span>
          <div class="br-item" role="listitem">
            <div class="row align-items-center">
              <div class="col-auto">
                <i class="fas fa-heartbeat" aria-hidden="true"></i>
              </div>
              <div class="col">Aluno 3</div>
            </div>
          </div>
          <span class="br-divider"></span>
        </div>
      </div>
      <div class="group">
        <div
          class="br-item"
          role="listitem"
          data-toggle="collapse"
          data-target="h2"
        >
          <div class="content">
            <div class="flex-fill">Aluno 2</div>
            <i class="fas fa-angle-down" aria-hidden="true"></i>
          </div>
        </div>
        <div class="br-list" id="h2" role="list" hidden="hidden">
          <div class="br-item" role="listitem">
            <div class="row align-items-center">
              <div class="col-auto">
                <i class="fas fa-heartbeat" aria-hidden="true"></i>
              </div>
              <div class="col">Sub-item</div>
            </div>
          </div>
          <span class="br-divider"></span>
          <div class="br-item" role="listitem">
            <div class="row align-items-center">
              <div class="col-auto">
                <i class="fas fa-heartbeat" aria-hidden="true"></i>
              </div>
              <div class="col">Sub-item</div>
            </div>
          </div>
          <span class="br-divider"></span>
          <div class="br-item" role="listitem">
            <div class="row align-items-center">
              <div class="col-auto">
                <i class="fas fa-heartbeat" aria-hidden="true"></i>
              </div>
              <div class="col">Sub-item</div>
            </div>
          </div>
          <span class="br-divider"></span>
        </div>
      </div>
      <div class="group">
        <div
          class="br-item"
          role="listitem"
          data-toggle="collapse"
          data-target="h3"
        >
          <div class="content">
            <div class="flex-fill">Aluno 3</div>
            <i class="fas fa-angle-down" aria-hidden="true"></i>
          </div>
        </div>
        <div class="br-list" id="h3" role="list" hidden="hidden">
          <div class="br-item" role="listitem">
            <div class="row align-items-center">
              <div class="col-auto">
                <i class="fas fa-heartbeat" aria-hidden="true"></i>
              </div>
              <div class="col">Sub-item</div>
            </div>
          </div>
          <span class="br-divider"></span>
          <div class="br-item" role="listitem">
            <div class="row align-items-center">
              <div class="col-auto">
                <i class="fas fa-heartbeat" aria-hidden="true"></i>
              </div>
              <div class="col">Sub-item</div>
            </div>
          </div>
          <span class="br-divider"></span>
          <div class="br-item" role="listitem">
            <div class="row align-items-center">
              <div class="col-auto">
                <i class="fas fa-heartbeat" aria-hidden="true"></i>
              </div>
              <div class="col">Sub-item</div>
            </div>
          </div>
          <span class="br-divider"></span>
        </div>
      </div>
    </div>

    // <div>
    //   <h2>Lista de Itens</h2>
    //   <ul>
    //     {items.map((item) => (
    //       <li
    //         key={item.id}
    //         onClick={() => handleClick(item)}
    //         style={{
    //           cursor: "pointer",
    //           backgroundColor: selectedItem === item ? "#f0f0f0" : "#fff",
    //         }}
    //       >
    //         {item.name}
    //       </li>
    //     ))}
    //   </ul>
    // </div>
  );
}
