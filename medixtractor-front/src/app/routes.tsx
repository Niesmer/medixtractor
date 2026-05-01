import { createBrowserRouter } from "react-router";
import { Layout } from "./components/Layout";
import { Home } from "./components/Home";
import { SearchResults } from "./components/SearchResults";
import { DrugDetail } from "./components/DrugDetail";
import { SignupPage } from "./components/SignupPage";
import { LoginPage } from "./components/LoginPage";
import { Favorites } from "./components/Favorites";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: Layout,
    children: [
      { index: true, Component: Home },
      { path: "search", Component: SearchResults },
      { path: "recherche", Component: SearchResults },
      { path: "drug/:id", Component: DrugDetail },
      { path: "medicament/:id", Component: DrugDetail },
      { path: "favorites", Component: Favorites },
      { path: "signup", Component: SignupPage },
      { path: "login", Component: LoginPage },
    ],
  },
]);

