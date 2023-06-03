import Head from "next/head";
import axios from "axios";
import { User } from "@/types/User";
import Link from "next/link";
import { GetServerSideProps } from "next";
import { authApi } from "@/ServerAPI/AuthAPI";
import { userApi } from "@/ServerAPI/UserAPI";
import { useRouter } from "next/router";
import { accessTokenManger } from "@/ServerAPI/AcessTokenManager";
import { useEffect } from "react";

interface AuthProps {
  user: User | null;
  accessToken: string | undefined;
}

export default function Home({ user, accessToken }: AuthProps) {
  useEffect(() => {
    accessTokenManger.setAccessToken(accessToken);
  }, []);

  const router = useRouter();
  const logout = async () => {
    const response = await authApi.logout();
    if (response.error === null) {
      router.push("/");
    }
  };
  return (
    <>
      <Head>
        <title>CodeDepo</title>
        <meta name="viewport" content="width=device-width, initial-scale=1" />
      </Head>
      <main>
        {user && <button onClick={logout}>Logout</button>}
        {user ? (
          <div>
            <h1>Zdravo {user.id}</h1>
            <Link href={"/dashboard"}>Dashboard</Link>
          </div>
        ) : (
          <div>
            <h1>Zdravo nepoznati korisnice.</h1>
            <Link href="/login">Login</Link>
          </div>
        )}
      </main>
    </>
  );
}
export const getServerSideProps: GetServerSideProps<{
  user: User | null;
  accessToken: string | undefined;
  error: string;
}> = async (context) => {
  const response = await authApi.refreshToken(
    context.req.cookies.refresh_token
  );
  let user: User | null = null;
  if (response.response) {
    user = (await userApi.getMe(response.response)).response;
  }
  return {
    props: {
      error: "",
      accessToken: response.response || null,
      user: user,
    },
  };
};
