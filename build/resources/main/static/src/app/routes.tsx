import { createBrowserRouter } from "react-router";
import { Layout } from "./components/Layout";
import { Home } from "./components/Home";
import { SearchResults } from "./components/SearchResults";
import { DrugDetail } from "./components/DrugDetail";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: Layout,
    children: [
      { index: true, Component: Home },
      { path: "search", Component: SearchResults },
      { path: "drug/:id", Component: DrugDetail },
    ],
  },
]);
