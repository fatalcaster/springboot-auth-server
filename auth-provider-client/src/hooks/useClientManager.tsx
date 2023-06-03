import { AccessTokenContext } from "@/components/AccessTokenContext";
import { clientApi } from "@/ServerAPI/ClientAPI";
import { Client } from "@/types/Client";
import {
  createContext,
  createRef,
  RefObject,
  useContext,
  useEffect,
  useState,
} from "react";

export enum ClientCreationState {
  NotEnteringName,
  EnteringName,
}

interface UseClientManagerProps {
  initialClients?: Client[];
  accessToken: string;
}

export const ClientManagerContext = createContext<ClientManagerOutputs>({
  cancelCreation: () => {},
  clientList: [],
  clientNameInputRef: null,
  createClient: async () => {},
  createClientState: ClientCreationState.NotEnteringName,
  deleteClient: async () => {},
  getMyClients: async () => {},
  regenerateSecret: async () => {},
});

interface ClientManagerOutputs {
  clientList: Client[];
  createClient: () => Promise<void>;
  deleteClient: (clientId: string) => Promise<void>;
  getMyClients: () => Promise<void>;
  createClientState: ClientCreationState;
  clientNameInputRef: RefObject<HTMLInputElement> | null;
  cancelCreation: () => void;
  regenerateSecret: (clientId: string) => Promise<void>;
}

export default function useClientManager({
  initialClients,
  accessToken,
}: UseClientManagerProps): ClientManagerOutputs {
  const [clientList, setClientList] = useState<Client[]>(initialClients || []);
  const [createClientError, setCreateClientError] = useState("");
  const [createClientState, setCreateClientState] = useState(
    ClientCreationState.NotEnteringName
  );
  const clientNameInputRef = createRef<HTMLInputElement>();
  useEffect(() => {
    getMyClients();
  }, []);

  const cancelCreation = () => {
    setCreateClientState(ClientCreationState.NotEnteringName);
  };

  async function regenerateSecret(clientId: string) {
    const response = await clientApi.genereateNewSecret(clientId, accessToken);
    if (response.response) {
      console.log(response.response);
      setClientList((prev) =>
        prev.map((client) => {
          if (client.client_id !== clientId) {
            return client;
          }
          return {
            ...client,
            client_secret: response.response?.client_secret,
          };
        })
      );
    } else {
      console.error(response.error);
    }
  }

  async function createClient() {
    if (createClientState === ClientCreationState.NotEnteringName) {
      setCreateClientState(ClientCreationState.EnteringName);
      return;
    }
    if (!clientNameInputRef.current) {
      return;
    }

    const clientName = clientNameInputRef.current.value;
    const response = await clientApi.createClient(clientName, accessToken);
    console.log(response.response);
    if (response.response) {
      setClientList([response.response, ...clientList]);
      setCreateClientState(ClientCreationState.NotEnteringName);
    }
  }

  async function getMyClients() {
    const c = await clientApi.getMyClients(accessToken);
    if (c.response) setClientList(c.response);
    // TODO handle error
  }
  const deleteClient = async (clientId: string) => {
    const response = await clientApi.deleteClient(clientId, accessToken);
    if (response?.error === null)
      setClientList(
        clientList.filter((client) => client.client_id !== clientId)
      );
  };

  return {
    clientList,
    createClient,
    deleteClient,
    getMyClients,
    createClientState,
    clientNameInputRef,
    cancelCreation,
    regenerateSecret,
  };
}
