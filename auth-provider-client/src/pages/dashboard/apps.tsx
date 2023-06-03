import { AccessTokenContext } from "@/components/AccessTokenContext";
import DashboardPageLayout, {
  MenuItem,
} from "@/components/DashboardPageLayout";
import {
  RefreshIcon,
  TrashIcon,
  VerticalOptions,
  VisibleIcon,
} from "@/components/Icons";
import useClientManager, {
  ClientCreationState,
  ClientManagerContext,
} from "@/hooks/useClientManager";
import { authApi } from "@/ServerAPI/AuthAPI";
import { clientApi } from "@/ServerAPI/ClientAPI";
import { Client } from "@/types/Client";
import {
  ContentBox,
  SmallButton,
  LargeInput,
} from "codedepo-themed-components-nextjs";
import { GetServerSideProps } from "next";
import Image from "next/image";
import { useContext, useMemo, useState } from "react";

export default function ClientDashboardPage({
  clients: clientsData,
  accessToken,
  errorMessage,
}: {
  errorMessage: string | null;
  clients: [];
  accessToken: string | null;
}) {
  const managerProps = useClientManager({
    initialClients: clientsData,
    accessToken: accessToken!,
  });
  return (
    <ClientManagerContext.Provider value={{ ...managerProps }}>
      <DashboardPageLayout currentPage={MenuItem.Apps}>
        {errorMessage ? (
          <ContentBox className="text-primary h-16 p-2 flex items-center justify-center ">
            <p className="bg-secondary-800 w-full text-center p-2">
              {errorMessage}
            </p>
          </ContentBox>
        ) : (
          <>
            <div className="">
              <SmallButton
                onClick={managerProps.createClient}
                color={
                  managerProps.createClientState ===
                  ClientCreationState.NotEnteringName
                    ? "secondary-800"
                    : "primary-500"
                }
                className="shadow-lg shadow-neutral-900"
              >
                {managerProps.createClientState ===
                ClientCreationState.NotEnteringName
                  ? "New App +"
                  : "Create App"}
              </SmallButton>

              {managerProps.createClientState ===
                ClientCreationState.EnteringName && (
                <>
                  <SmallButton
                    color="secondary-800"
                    className="ml-6"
                    onClick={managerProps.cancelCreation}
                  >
                    Cancel
                  </SmallButton>
                  <ContentBox className="block mt-6 p-2 py-3">
                    <LargeInput
                      ref={managerProps.clientNameInputRef}
                      className="pl-2 w-full"
                      placeholder="App Name"
                    />
                  </ContentBox>
                </>
              )}
            </div>
            <ul className="my-12  gap-4 flex flex-col  w-full">
              {managerProps.clientList.map((client) => {
                return (
                  <li key={client.client_id}>
                    <ClientBox client={client} />
                  </li>
                );
              })}
            </ul>
          </>
        )}
      </DashboardPageLayout>
    </ClientManagerContext.Provider>
  );
}

interface ClientBoxProps {
  client: Client;
}

enum AppOption {
  None,
  ViewId,
  CreateNewSecret,
  DeleteApp,
}

function ClientBox({ client }: ClientBoxProps) {
  enum ClientBoxView {
    INITIAL,
    CONFIRMATION,
  }

  // use reducer here
  const [confirmOption, setConfirmOption] = useState<AppOption>(AppOption.None);

  const setInitialView = () => {
    setConfirmOption(AppOption.None);
  };
  const { deleteClient, regenerateSecret, clientList } =
    useContext(ClientManagerContext);
  const ConfirmationViewProps = useMemo(
    () =>
      new Map<AppOption, ClientBoxConfirmationViewProps>([
        [
          AppOption.CreateNewSecret,
          {
            prompt: "Are you sure you want to create a new secret?",
            onConfirm: () => {
              regenerateSecret(client.client_id);
              console.log(clientList);
              setInitialView();
            },
            onCancel: setInitialView,
          },
        ],
        [
          AppOption.ViewId,
          {
            prompt: client.client_id,
            onConfirm: setInitialView,
            onCancel: null,
          },
        ],
        [
          AppOption.DeleteApp,
          {
            prompt: "Are you sure you want to delete the app?",
            onConfirm: () => deleteClient(client.client_id),
            onCancel: setInitialView,
          },
        ],
      ]),
    []
  );
  console.log("RERENDERED " + client.client_secret);
  if (confirmOption === AppOption.None)
    return (
      <ClientBoxInitialView
        client={client}
        setConfirmOption={setConfirmOption}
      />
    );
  if (confirmOption !== null)
    return (
      <ClientBoxConfirmationView
        {...ConfirmationViewProps.get(confirmOption)!}
      />
    );
  return null;
}

interface ClientBoxConfirmationViewProps {
  prompt: string;
  onConfirm: (() => void) | null;
  onCancel: (() => void) | null;
}
function ClientBoxConfirmationView({
  prompt,
  onConfirm,
  onCancel,
}: ClientBoxConfirmationViewProps) {
  console.log("PROOS");
  return (
    <ContentBox className="flex justify-between h-20 items-center p-4">
      <p>{prompt}</p>
      <div className="flex gap-2">
        {onConfirm && (
          <SmallButton color="secondary-900" onClick={onConfirm}>
            Okay
          </SmallButton>
        )}
        {onCancel && (
          <SmallButton color="primary-500" onClick={onCancel}>
            Cancel
          </SmallButton>
        )}
      </div>
    </ContentBox>
  );
}

function ClientBoxInitialView({
  client,
  setConfirmOption,
}: ClientBoxProps & { setConfirmOption: (option: AppOption) => void }) {
  const [isSecretRevealed, setIsSecretRevealed] = useState(false);
  const options = [
    {
      icon: <VisibleIcon />,
      onClick: () => setConfirmOption(AppOption.ViewId),
      tooltip: "View app id",
    },
    {
      icon: <RefreshIcon />,
      onClick: () => setConfirmOption(AppOption.CreateNewSecret),
      tooltip: "Refresh secret",
    },
    {
      icon: <TrashIcon />,
      onClick: () => setConfirmOption(AppOption.DeleteApp),
      tooltip: "Delete app",
    },
  ];
  return (
    <ContentBox className="w-full h-20 p-2 flex items-center justify-between gap-5">
      <div className="flex items-center">
        <Image
          alt="app logo"
          src="/logo.svg"
          width={100}
          height={100}
          className="h-16 w-16 bg-secondary-800 rounded-full border-2 border-primary"
        />
        <div className="ml-4 ">
          <p className="text-secondary-300">{client.client_name}</p>
          {client.client_secret && (
            <p>
              <span className="select-none mr-2">secret:</span>
              <span
                onClick={() => setIsSecretRevealed(true)}
                className={`bg-secondary-800 text-primary ${
                  !isSecretRevealed && "blur-sm"
                }`}
              >
                {client.client_secret}
              </span>
            </p>
          )}
        </div>
      </div>
      <div className="flex flex-nowrap flex-row gap-1">
        {options.map((option, index) => (
          <button
            data-tooltip={option.tooltip}
            onClick={option.onClick}
            key={index}
            className="bg-secondary-800 p-1  text-secondary-300 hover:brightness-125"
          >
            {option.icon}
          </button>
        ))}
      </div>
    </ContentBox>
  );
}

export const getServerSideProps: GetServerSideProps<{
  clients: Client[];
  errorMessage: string | null;
  accessToken: string;
}> = async (context) => {
  const response = await authApi.refreshToken(
    context.req.cookies.refresh_token
  );
  let clients: Client[] = [];
  if (response.response)
    clients = (await clientApi.getMyClients(response.response)).response || [];
    console.log(clients);
  console.log("TESTIRAM", response.error);
  return {
    props: {
      clients: clients,
      errorMessage: "",
      accessToken: response.response!,
    },
  };
};
