import Cookie from "@/helpers/Cookie";
import { userApi } from "@/ServerAPI/UserAPI";
import { User } from "@/types/User";
import axios from "axios";
import { useEffect, useState } from "react";

interface UseUserManagerProps {
  initialUsers?: User[];
}
export default function useUserManager({ initialUsers }: UseUserManagerProps) {
  const [userList, setUserList] = useState<User[]>(initialUsers || []);

  async function getAllUsers(page: number = 0) {
    const c = await userApi.getAllUsers();
    if (c.response) setUserList(c.response);
    // TODO handle error
  }
  // const deleteUser = async (userId: string) => {
  //   const response = await userApi.deleteUser(
  //     userId,
  //     Cookie.getAccessToken() || ""
  //   );
  //   if (response?.error === null)
  //     setUserList(userList.filter((user) => user.user_id !== userId));
  // };

  return {
    userList,
    getAllUsers,
  };
}
