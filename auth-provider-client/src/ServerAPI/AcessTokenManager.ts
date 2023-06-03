class AccessTokenManger {
  private accessToken: string | undefined;

  public getAccessToken() {
    return this.accessToken;
  }
  public setAccessToken(accessToken: string | undefined | null) {
    if (typeof accessToken === "string") this.accessToken = accessToken;
    else this.accessToken = undefined;
  }
}
const accessTokenManger = new AccessTokenManger();

export { accessTokenManger };
