import { User } from "@/types/User";
import axios from "axios";
import { useEffect, useState } from "react";

export default function ClientTable() {
  const [clients, setClients] = useState<User[]>([]);

  useEffect(() => {
    const getClients = async () => {
      try {
        const res = await axios.get(
          "http://192.168.0.12:5000/api/user/all?page=0",
          {
            withCredentials: true,
          }
        );
        setClients(res.data as User[]);
      } catch (err) {}
    };
    getClients();
  }, []);

  return (
    <div>
      {clients.map((client) => (
        <p key={client.id}>{client.email}</p>
      ))}
    </div>
  );
}
