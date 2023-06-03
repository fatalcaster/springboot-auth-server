import Cookie from "@/helpers/Cookie";
import { User } from "@/types/User";
import axios from "axios";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";

// export default function useUserProfile(requireLogin: boolean = false) {
//   const [user, setUser] = useState<User | null>(null);
//   const [isAdmin, setIsAdmin] = useState<boolean>(false);
//   const router = useRouter();
//   const fetchUserProfile = async () => {
//     try {
//       const res = await axios.get("http://localhost:5000/api/user/me", {
//         withCredentials: true,
//         headers: { Authorization: Cookie.getAccessToken() },
//       });
//       console.log(res.data);
//       setUser(res.data as User);
//       if (res.data.roles.includes("ROLE_ADMIN")) {
//         console.log(res.data.roles);
//         setIsAdmin(true);
//       }
//     } catch (err) {
//       //   console.log((err as any).message);
//       if (requireLogin) {
//         router.push("/login");
//       }
//     }
//   };
//   useEffect(() => {
//     fetchUserProfile();
//   }, []);

//   return { user, logout, isAdmin };
// }
