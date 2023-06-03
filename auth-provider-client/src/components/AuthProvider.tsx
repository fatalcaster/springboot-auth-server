import { User } from "@/types/User";
import { createContext } from "react";

export const AuthenticatedContext = createContext<User | null>(null);
