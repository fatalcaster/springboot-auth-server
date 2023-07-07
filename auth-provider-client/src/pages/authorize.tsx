import Cookie from "@/helpers/Cookie";
import { authApi } from "@/ServerAPI/AuthAPI";
import { axiosClientInstance } from "@/ServerAPI/axios-client-instance";
import { clientApi } from "@/ServerAPI/ClientAPI";
import { userApi } from "@/ServerAPI/UserAPI";
import { Client } from "@/types/Client";
import { User } from "@/types/User";
import { ContentBox, LargeButton } from "codedepo-themed-components-nextjs";
import { GetServerSideProps } from "next";
import Image from "next/image";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";

interface AuthorizePageProps {
  authorizedUser: User | null;
  accessToken: string | null;
}

export default function AuthorizePage({
  authorizedUser,
  accessToken,
}: AuthorizePageProps) {
  const [user, setUser] = useState(authorizedUser);
  const [app, setApp] = useState<Client | null>(null);
  const router = useRouter();
  const { client_id } = router.query;
  const oauth2Params = router.query;
  
  useEffect(() => {
    const getClient = async () => {
      console.log(client_id);
      const response = await clientApi.getClientById(
        client_id as string,
        accessToken!
      );
      if (response.response) setApp(response.response);
    };
    getClient();
    if (!authorizedUser) {
      router.push("/login?redirect=" + encodeURIComponent(router.asPath));
    }
  }, []);

  const authorize = async () => {
    try {
      const response = await axiosClientInstance.get(
        `http://${process.env.NEXT_PUBLIC_OAUTH_PROVIDER_URL}/api/oauth2/authorize?response_type=${oauth2Params.response_type}&redirect_uri=${encodeURIComponent(oauth2Params.redirect_uri)}&client_id=${oauth2Params.client_id}&scope=${oauth2Params.scope}`,
        {
          withCredentials: true,
        }
      );
      // console.log(response.request);
      router.push(response.data);
    } catch (err) {
      // console.log(err);
    }
  };
  if (!user) {
    return <div>Loading...</div>;
  }
  console.log(oauth2Params.scope);
  return (
    <main className="fixed w-full flex items-center justify-center h-full">
      <ContentBox className="w-96 grid place-items-center p-4">
        <div className="w-32 h-32 bg-secondary-800 rounded-full border-4 border-primary flex justify-center items-center">
          <Image
            alt="app logo"
            src={app ? "/logo.svg" : "/loader.svg"}
            width={150}
            height={150}
            className="h-10 w-auto"
          />
        </div>
        {app && <h1 className="m-2 text-secondary-300 ">{app.client_name}</h1>}
        <p>Wants to access the following information</p>
        <div className="bg-secondary-800 w-full p-2 my-4 text-primary">
          <p>{oauth2Params.scope}</p>
        </div>

        <LargeButton color="primary-500" onClick={authorize}>Allow</LargeButton>
      </ContentBox>
    </main>
  );
}
export const getServerSideProps: GetServerSideProps<
  AuthorizePageProps
> = async (context) => {
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
      authorizedUser: user,
    },
  };
};
