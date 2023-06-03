import API from "./API";
import { axiosClientInstance } from "./axios-client-instance";

class AuthAPI extends API {
  public constructor() {
    super();
  }
  public async refreshToken(refreshToken?: string) {
    try {
      const response = await axiosClientInstance.post(
        "api/auth/refresh",
        {},
        {
          ...(refreshToken && {
            headers: { Cookie: `refresh_token=${refreshToken};` },
          }),
        }
      );
      return {
        error: null,
        response: response.data,
      };
    } catch (error) {
      return {
        error: this.validError(error),
        response: null,
      };
    }
  }
  public async logout() {
    try {
      await axiosClientInstance.post(
        "api/auth/logout",
        {},
        { withCredentials: true }
      );
      return {
        error: null,
        response: null,
      };
    } catch (error) {
      return {
        error: this.validError(error),
        response: null,
      };
    }
  }
  public async login(email: string, password: string) {
    const res = await super.handleRequest(
      async () =>
        await axiosClientInstance.post("api/auth/login", {
          email: email,
          password: password,
        })
    );
    console.log(res);
    return res;
  }

  public async register(email: string, password: string) {
    const res = await super.handleRequest(
      async () =>
        await axiosClientInstance.post("api/auth/register", {
          email: email,
          password: password,
        })
    );
    return res;
  }
}

const authApi = new AuthAPI();

export { authApi };
