// export const auth = getAuth(app);

// // Logout function
// export function logout() {
//   const logoutButton = document.querySelector('#logout');
//   logoutButton.addEventListener('click', (e) => {
//     e.preventDefault();
//     auth.signOut().then(() => {
//       console.log('user signed out');
//     });
//   });
// }

//logout 
const logout = document.querySelector('#logout');
logout.addEventListener('click', (e) => {
  e.preventDefault();
  auth.signOut().then(() => {
    console.log('user signed out');
  })
});
