import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import api from "../api/products"
import './App.css';
import Header from './Header';
import AddProduct from './AddProduct';
import ProductList from './ProductList';
import * as myConstants from './Constants'
import ProductDetail from "./ProductDetails";
import EditProduct from "./EditProduct";
import Auth from "./auth/Auth";


function App(props) {
  const [products, setProducts] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [searchResult, setSearchResult] = useState([]);

  console.log(localStorage.getItem("token"));

  const [isAuth, setIsAuth] = useState(localStorage.getItem("token") !== null && localStorage.getItem("token") !== "");

  useEffect(() => {
    const getAllProducts = async () => {
      if (!isAuth) {
        return;
      }
      const response = await api.get("/products")

      if (response.status !== 200) {
        alert("Couldn't retrieve products");
        return;
      }

      if (response.data) {
        setProducts(response.data);
      }

    };
    getAllProducts();
  }, [isAuth]);

  // useEffect(() => {
  //   localStorage.setItem(PRODUCTS_LS_KEY, JSON.stringify(products))
  // }, [products]);

  const login = async (user, token) => {

    let response

    try {
      response = await api.post(`/users/login`, user);
    } catch (err) {
      console.log(err)
    }

    if (!response || response.status !== 200) {
      alert("Please check your username and password")
      return;
    }
    console.log(response.data);
    localStorage.setItem("token", token);
    localStorage.setItem("username", user.username);
    setIsAuth(true);
  }

  const signUp = async (user) => {

    console.log(`going to add the user ${JSON.stringify(user)}`);
    const response = await api.post("/users", user);

    if (response.status !== 200 && response.status !== 201) {
      alert("Couldn't add a new user")
      return false;
    };

    return true;
    // login(user, "ok")
  }

  if (!isAuth) {
    return (<Auth login={login} signUp={signUp} />)
  }

  const addProductHandler = async (product) => {
    console.log(`going to add the product ${JSON.stringify(product)}`);
    const request = {
      id: (products.length + 10),
      ...product
    };
    const username = localStorage.getItem("username")
    const response = await api.post(`/products?username=${username}`, request)
    if (response.status !== 200 && response.status !== 201) {
      alert("Couldn't add the product")
      return
    }
    setProducts([...products, response.data])
  };

  const updateProductHandler = async (product) => {
    console.log(`going to edit the product ${JSON.stringify(product)}`)

    const response = await api.put(`/products/${product.id}`, product)
    if (response.status !== 200) {
      alert("Couldn't update the product")
      return false
    }
    setProducts(
      products.map((p) => {
        return p.id === product.id ? { ...product } : p;
      })
    );
    return true
  };

  const removeProductHandler = async (id) => {
    console.log(`going to remove the product with id = ${id}`)
    const response = await api.delete(`products/${id}`)
    if (response.status !== 200) {
      alert("Couldn't delete the product")
      return
    }
    const newProducts = products.filter((product) => { return product.id !== id })
    setProducts(newProducts)
  };

  const searchHandler = (searchT) => {
    setSearchTerm(searchT);
    if (searchT !== "") {
      const resultProducts = products.filter((product) => {
        return Object
          .values(product)
          .join(" ")
          .toLowerCase()
          .includes(searchT.toLowerCase())
      });

      setSearchResult(resultProducts);
    } else {
      setSearchResult(products);
    }
  }



  return (
    <div className='ui container'>
      <Router>
        <Header />
        <Routes>
          <Route path={myConstants.HOME} exact
            element={
              <ProductList products={searchTerm.length < 1 ? products : searchResult}
                term={searchTerm}
                searchHandler={searchHandler} />}
          />
          <Route
            path={myConstants.ADD_PRODUCT}
            element={<AddProduct
              addProductHandler={addProductHandler} />}
          />
          <Route
            path="/product/:id"
            element={<ProductDetail {...props} removeProductHandler={removeProductHandler} />}
          />
          <Route
            path={`${myConstants.EDIT_PRODUCT}/:id`}
            element={<EditProduct updateProductHandler={updateProductHandler} />}
          />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
