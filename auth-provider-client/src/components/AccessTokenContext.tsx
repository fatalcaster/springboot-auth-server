import { accessTokenManger } from "@/ServerAPI/AcessTokenManager";
import { createContext, useEffect, useState } from "react";

export const AccessTokenContext = createContext<{ accessToken?: string }>({
  accessToken: "",
});

function AccessTokenProvider({ accessToken }: { accessToken?: string }) {
  useEffect(() => {
    accessTokenManger.setAccessToken(accessToken);
  }, []);
}
