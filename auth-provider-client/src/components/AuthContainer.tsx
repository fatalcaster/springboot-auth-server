import { RequestResponse } from "@/ServerAPI/API";
import { authApi } from "@/ServerAPI/AuthAPI";
import axios from "axios";
import {
  ContentBox,
  DefaultInput,
  LargeInput,
  MediumButton,
  SmallButton,
} from "codedepo-themed-components-nextjs";
import Image from "next/image";
import Link from "next/link";
import { useRouter } from "next/router";
import {
  ChangeEventHandler,
  createRef,
  forwardRef,
  ReactNode,
  useContext,
  useEffect,
  useRef,
  useState,
} from "react";
import { AuthenticatedContext } from "./AuthProvider";
import { EnvelopeIcon, InvisibleIcon, LockIcon, VisibleIcon } from "./Icons";

export enum AuthType {
  LOGIN,
  REGISTER,
}

interface AuthContainerProps {
  authType: AuthType;
  redirectUri: string;
}

type schema = {
  label: string;
  endpoint: (
    email: string,
    password: string
  ) => Promise<RequestResponse<unknown>>;
  expectedStatus: number;
  callToAction: ReactNode;
};
const registerSchema: schema = {
  label: "Register",
  endpoint: (email, password) => authApi.register(email, password),
  expectedStatus: 201,
  callToAction: (
    <span className="text-secondary-300">
      Already have an account?{" "}
      <span className="ml-1">
        <Link href="/login" className="text-primary active:underline">
          Sign in!
        </Link>
      </span>
    </span>
  ),
};

const loginSchema: schema = {
  label: "Login",
  endpoint: (email, password) => authApi.login(email, password),
  expectedStatus: 200,
  callToAction: (
    <span className="text-secondary-300">
      Don't have an account?{" "}
      <span className="ml-1 ">
        <Link href="/register" className="text-primary active:underline">
          Sign up!
        </Link>
      </span>
    </span>
  ),
};

export default function AuthContainer({
  redirectUri,
  authType,
}: AuthContainerProps) {
  const [schema, _] = useState<schema>(
    authType === AuthType.LOGIN ? loginSchema : registerSchema
  );
  const [isPasswordVisible, setIsPasswordVisible] = useState(false);
  const togglePasswordVisible = () => setIsPasswordVisible((prev) => !prev);

  const emailRef = createRef<HTMLInputElement>();
  const passwordRef = createRef<HTMLInputElement>();

  const router = useRouter();

  const user = useContext(AuthenticatedContext);

  useEffect(() => {
    if (user) {
      router.push(redirectUri);
    }
  }, [user]);

  const onSubmit = async () => {
    if (!emailRef.current || !passwordRef.current) return;
    const response = await schema.endpoint(
      emailRef.current.value,
      passwordRef.current.value
    );
    if (!response.error) {
      router.push(redirectUri);
    }
    // else console.log(response.error);
  };

  return (
    <ContentBox className="w-96 h-96 grid  justify-items-center">
      <div className="mt-8 bg-secondary-800 p-4 w-full flex flex-col items-center">
        <Image alt="CodeDepo" src="/logo.svg" width={150} height={150} />
        {schema.callToAction}
      </div>
      <form onSubmit={onSubmit}>
        <div className="flex flex-col items-center self-end my-4">
          <label>
            <p>Email</p>
            <div className="relative">
              <EnvelopeIcon className="absolute text-secondary-300 left-2 top-2" />
              <LargeInput ref={emailRef} type="email" className="p-1.5 pl-10" />
            </div>
          </label>
          <label>
            <p>Password</p>
            <div className="relative">
              <LockIcon className="absolute text-secondary-300 left-2 top-2" />
              <LargeInput
                className="p-1.5 pl-10"
                ref={passwordRef}
                type={isPasswordVisible ? "text" : "password"}
              />
              <button
                type="button"
                className="absolute right-2 top-2 text-secondary-300"
                onClick={togglePasswordVisible}
              >
                {isPasswordVisible ? <InvisibleIcon /> : <VisibleIcon />}
              </button>
            </div>
          </label>
          <SmallButton
            type="button"
            color="primary-500"
            className="w-28 h-8 block mt-6"
            onClick={onSubmit}
          >
            {schema.label}
          </SmallButton>
        </div>
      </form>
    </ContentBox>
  );
}
