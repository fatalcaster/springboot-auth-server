import axios from "axios";
import { accessTokenManger } from "./AcessTokenManager";
import { authApi } from "./AuthAPI";

const axiosClientInstance = axios.create({
  baseURL: typeof window === undefined ? "http://ingress-nginx-controller.ingress-nginx.svc.cluster.local/" : "http://accounts.codedepo.com/",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

axiosClientInstance.interceptors.request.use(
  (config) => {
    // config.
    const accessToken = config.data?.accessToken;
    if (accessToken) {
      config.headers["Authorization"] = accessToken;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axiosClientInstance.interceptors.response.use(
  (res) => {
    return res;
  },
  async (err) => {
    // console.log("test " + err);

    const initConfig = err.config;
    if (err.response) {
      if (err.response.status === 401 && !initConfig._retry) {
        initConfig._retry = true;
        try {
          const accessToken = await authApi.refreshToken();
          axiosClientInstance.defaults.headers.common["Authorization"] =
            accessToken.response;
          console.log("ACCESS", accessToken);
          // don't do this on the server, it can accidentally swap tokens
          if (typeof window !== undefined)
            accessTokenManger.setAccessToken(accessToken.response);

          
          return axiosClientInstance(initConfig);
        } catch (_err) {
          Promise.reject(_err);
        }
      }
      if (err.response.status === 403 && err.response.data) {
        return Promise.reject(err.response.data);
      }
    }
    return Promise.reject(err);
  }
);

export { axiosClientInstance };
