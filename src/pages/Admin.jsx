import Header from "../components/Header";
import { useEffect } from "react";
import List from "../components/List";

export default function Admin() {
  useEffect(() => {
    const user = JSON.parse(sessionStorage.getItem("user"));
    if (!user || !user.authToken) {
      window.location.href = "/main";
    }
  }, []);

  return (
    <div>
      <Header />
      <List />
    </div>
  );
}
