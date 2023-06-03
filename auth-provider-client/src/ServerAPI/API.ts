export default class API {
  protected validError = function (error: unknown): string {
    if (error && typeof error === "object" && "message" in error)
      return error.message as string;
    return "Unknown error";
  };

  protected async handleRequest<T>(
    fn: () => Promise<{ data: T }>
  ): Promise<RequestResponse<T>> {
    try {
      return { response: (await fn()).data, error: null };
    } catch (error) {
      // if()
      console.log(error);
      return { response: null, error: this.validError(error) };
    }
  }
}

export interface RequestResponse<T> {
  error: string | null;
  response: T | null;
}
