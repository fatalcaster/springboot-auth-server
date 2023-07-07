import { User } from "@/types/User";
import { AxiosRequestConfig } from "axios";
import API from "./API";
import { axiosClientInstance } from "./axios-client-instance";

class UserAPI extends API {
  constructor() {
    super();
  }
  public async getMe(accessToken?: string) {
    return await this.handleRequest<User>(
      async () =>
        await axiosClientInstance.get("api/user/me", { data: { accessToken } })
    );
  }

  public async getAllUsers(page: number = 0, accessToken?: string) {
    return await this.handleRequest<User[]>(
      async () =>
        await axiosClientInstance.get(`api/user/all?page=${page}`, {
          data: { accessToken },
        })
    );
  }
}
const userApi = new UserAPI();
export { userApi };
