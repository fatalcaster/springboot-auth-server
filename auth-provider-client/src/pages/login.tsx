import AuthContainer, { AuthType } from "@/components/AuthContainer";
import { AuthenticatedContext } from "@/components/AuthProvider";
import { userApi } from "@/ServerAPI/UserAPI";
import { User } from "@/types/User";
import { GetServerSideProps } from "next";
import Head from "next/head";
import { useRouter } from "next/router";

export default function LoginPage({ user }: { user: User | null }) {
  const router = useRouter();
  const { redirect } = router.query;
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
  const user = (await userApi.getMe()).response;

  return {
    props: {
      user: user,
    },
  };
};
