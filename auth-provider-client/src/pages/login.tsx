import AuthContainer, { AuthType } from "@/components/AuthContainer";
import { AuthenticatedContext } from "@/components/AuthProvider";
import { authApi } from "@/ServerAPI/AuthAPI";
import { userApi } from "@/ServerAPI/UserAPI";
import { User } from "@/types/User";
import { GetServerSideProps } from "next";
import Head from "next/head";
import { useRouter } from "next/router";
import {useEffect} from "react";

export default function LoginPage({ user }: { user: User | null }) {
  const router = useRouter();
  const { redirect } = router.query;

  useEffect(() => {
    console.log("REDIRECT:", redirect);
    if(user&&redirect) {
      router.push(redirect);
    }
  },[])
 
  return (
    <>
      <Head>
        <title>Login</title>
        <meta name="viewport" content="width=device-width, initial-scale=1" />
      </Head>
      <AuthenticatedContext.Provider value={user}>
        <main className="flex justify-center fixed w-screen h-screen items-center">
          <AuthContainer
            redirectUri={(redirect as string) || "/"}
            authType={AuthType.LOGIN}
          />
        </main>
      </AuthenticatedContext.Provider>
    </>
  );
}

export const getServerSideProps: GetServerSideProps<{
  user: User | null;
}> = async (context) => {
  const response = await authApi.refreshToken(
    context.req.cookies.refresh_token
  );
  let user: User | null = null;
  if (response.response) {
    user = (await userApi.getMe(response.response)).response;
  }
  console.log(user);
  return {
    props: {
      user: user,
    },
  };
};
