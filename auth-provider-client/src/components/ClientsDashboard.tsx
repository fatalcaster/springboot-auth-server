import Cookie from "@/helpers/Cookie";
import { User } from "@/types/User";
import axios from "axios";
import { useEffect, useState } from "react";

export default function ClientTable() {
  // const [clients, setClients] = useState<User[]>([]);
  // useEffect(() => {
  //   const getClients = async () => {
  //     try {
  //       const res = await axios.get(
  //         "http://localhost:5000/api/user/all?page=0",
  //         {
  //           headers: { Authorization: Cookie.getClientAccessToken() },
  //           withCredentials: true,
  //         }
  //       );
  //       setClients(res.data as User[]);
  //       console.log(res.data);
  //     } catch (err) {
  //       console.log((err as any).message);
  //     }
  //   };
  //   getClients();
  // }, []);
  // return (
  //   <div>
  //     {clients.map((client) => (
  //       <p key={client.id}>{client.email}</p>
  //     ))}
  //   </div>
  // );
}
