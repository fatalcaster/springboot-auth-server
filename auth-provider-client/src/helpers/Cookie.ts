import { AxiosRequestHeaders, AxiosResponse } from "axios";
import { NextRequest } from "next/server";

export default class Cookie {
  public static getClientCookie = (key: string, source = document.cookie) =>
    (new RegExp((key || "=") + "=(.*?); ", "gm").exec(
      document.cookie + "; "
    ) || ["", null])[1];

  public static getAccessToken() {
    if (typeof window !== "undefined")
      return Cookie.getClientCookie("access_token");
    return null;
  }
  public static getCookieFromAxiosResponse = (
    response: AxiosResponse<any, any>
  ) => {
    return (
      (response.headers["set-cookie"] as string[])
        .find((cookie) => cookie.includes("access_token"))
        ?.match(new RegExp(`^${"access_token"}=(.+?);`))?.[1] || null
    );
  };
  // public static getServerAccessCookie = (request: AxiosRequestHeaders) => {
  //   console.log(request.get("set-cookie"));
  //   return null;
  //   // return (
  //   //   (response.headers["set-cookie"] as string[])
  //   //     .find((cookie) => cookie.includes("access_token"))
  //   //     ?.match(new RegExp(`^${"access_token"}=(.+?);`))?.[1] || null
  //   // );
  // };
}
