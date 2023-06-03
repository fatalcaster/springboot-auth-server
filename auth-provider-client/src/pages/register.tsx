import AuthContainer, { AuthType } from "@/components/AuthContainer";
import axios from "axios";
import Head from "next/head";
import { ChangeEventHandler, useState } from "react";

export default function RegisterPage() {
  return (
    <>
      <Head>
        <title>Register</title>
        <meta name="viewport" content="width=device-width, initial-scale=1" />
      </Head>
      <main className="flex justify-center fixed w-screen h-screen items-center">
        <AuthContainer redirectUri="/" authType={AuthType.REGISTER} />
      </main>
    </>
  );
}
