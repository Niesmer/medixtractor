import { createBrowserRouter } from "react-router";
import { Layout } from "./components/Layout";
import { Home } from "./components/Home";
import { SearchResults } from "./components/SearchResults";
import { DrugDetail } from "./components/DrugDetail";
import { SignupPage } from "./components/SignupPage";
import { LoginPage } from "./components/LoginPage";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: Layout,
    children: [
      { index: true, Component: Home },
      { path: "search", Component: SearchResults },
      { path: "recherche", Component: SearchResults }, // Support old URL
      { path: "drug/:id", Component: DrugDetail },
      { path: "medicament/:id", Component: DrugDetail }, // Support old URL
      { path: "signup", Component: SignupPage },
      { path: "login", Component: LoginPage },
    ],
  },
]);

