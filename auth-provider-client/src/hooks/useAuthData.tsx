import { User } from "@/types/User";
import { useState } from "react";

export default function useUserProfile(
  userInfo: User | null,
  accessTokenData: String | null
) {
  const [user, setUser] = useState<User | null>(userInfo);
  const [accessToken, setAccessToken] = useState<String | null>(
    accessTokenData
  );

  return {
    user,
    setUser,
    accessToken,
    setAccessToken,
  };
}
