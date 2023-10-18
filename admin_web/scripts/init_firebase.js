// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import { getAuth } from "firebase/auth";
import { getFirestore, collection, getDocs } from 'firebase/firestore/lite';
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyB798vUW1sB8LH4at3MmXkcMQtyJgw_mG4",
  authDomain: "travel-assistant-app-4b593.firebaseapp.com",
  databaseURL: "https://travel-assistant-app-4b593-default-rtdb.asia-southeast1.firebasedatabase.app",
  projectId: "travel-assistant-app-4b593",
  storageBucket: "travel-assistant-app-4b593.appspot.com",
  messagingSenderId: "760530191338",
  appId: "1:760530191338:web:1dd8ca6dc4f3ee0b18a893",
  measurementId: "G-72L8S5WCDL"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const auth = getAuth(app);
const db = getFirestore(app);

