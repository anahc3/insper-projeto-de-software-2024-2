// src/aposta/ListaAposta.jsx
import { useKeycloak } from "@react-keycloak/web";
import { useEffect, useState } from "react";

function ListaAposta() {
    const { keycloak, initialized } = useKeycloak();
    const [apostas, setApostas] = useState([]);

    useEffect(() => {
        if (initialized && keycloak.authenticated) {
            fetch("http://localhost:8080/apostas", {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${keycloak.token}`,
                },
            })
            .then(response => response.json())
            .then(data => setApostas(data))
            .catch(error => console.error("Erro ao carregar apostas:", error));
        }
    }, [initialized, keycloak]);

    return (
        <div>
            <h2>Listagem de Apostas</h2>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Status</th>
                        <th>Data da Aposta</th>
                    </tr>
                </thead>
                <tbody>
                    {apostas.map((aposta) => (
                        <tr key={aposta.id}>
                            <td>{aposta.id}</td>
                            <td>{aposta.status}</td>
                            <td>{new Date(aposta.dataAposta).toLocaleDateString()}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default ListaAposta;