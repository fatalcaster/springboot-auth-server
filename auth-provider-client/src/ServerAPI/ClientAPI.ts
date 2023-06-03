import { Client } from "@/types/Client";
import { User } from "@/types/User";
import API from "./API";
import { axiosClientInstance } from "./axios-client-instance";

class ClientApi extends API {
  constructor() {
    super();
  }
  public async genereateNewSecret(clientId: string, accessToken: string) {
    return await this.handleRequest<Client>(
      async () =>
        await axiosClientInstance.put(`api/client/${clientId}/secret`, {
          data: {
            accessToken: accessToken,
          },
        })
    );
  }
  public async getClientById(clientId: string, accessToken: string) {
    return await this.handleRequest<Client>(
      async () =>
        await axiosClientInstance.get(`api/client/${clientId}`, {
          data: {
            accessToken: accessToken,
          },
        })
    );
  }
  public async getMyClients(accessToken: string) {
    return await this.handleRequest<Client[]>(
      async () =>
        await axiosClientInstance.get("api/client", {
          data: { accessToken: accessToken },
        })
    );
  }
  public async createClient(clientName: string, accessToken: string) {
    return await this.handleRequest<Client>(
      async () =>
        await axiosClientInstance.post(
          "api/client",
          { name: clientName },
          { data: { accessToken: accessToken } }
        )
    );
  }
  public async deleteClient(clientId: string, accessToken: string) {
    if (!clientId) return;
    return await this.handleRequest<Client[]>(
      async () =>
        await axiosClientInstance.delete(`api/client/${clientId}`, {
          data: { accessToken: accessToken },
        })
    );
  }
}
const clientApi = new ClientApi();
export { clientApi };
