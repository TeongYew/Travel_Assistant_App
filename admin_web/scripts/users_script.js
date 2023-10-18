// Initialize Firebase (your Firebase configuration goes here)
firebase.initializeApp(config);

// Reference to the Firestore database
var db = firebase.firestore();

// DOM elements
var userTable = document.getElementById('userTable');
var searchInput = document.getElementById('search');
var addUserButton = document.getElementById('addUser');
var addUserModal = document.getElementById('addUserModal');
var closeModal = document.getElementById('closeModal');
var newUsername = document.getElementById('newUsername');
var newEmail = document.getElementById('newEmail');
var saveUserButton = document.getElementById('saveUser');

// Function to render user data in the table
function renderUser(doc) {
    var tr = document.createElement('tr');
    tr.setAttribute('data-id', doc.id);
    tr.innerHTML = '<td>' + doc.data().uid + '</td>' +
                  '<td>' + doc.data().username + '</td>' +
                  '<td>' + doc.data().email + '</td>' +
                  '<td><button class="edit">Edit</button> <button class="delete">Delete</button></td>';
    userTable.appendChild(tr);
}

// Real-time listener for user data
db.collection('users').onSnapshot(snapshot => {
    var changes = snapshot.docChanges();
    changes.forEach(change => {
        if (change.type == 'added') {
            renderUser(change.doc);
        } else if (change.type == 'removed') {
            var tr = userTable.querySelector('[data-id=' + change.doc.id + ']');
            userTable.removeChild(tr);
        }
    });
});

// Search functionality
searchInput.addEventListener('keyup', function() {
    var searchTerm = searchInput.value.toLowerCase();
    var rows = userTable.getElementsByTagName('tr');
    for (var i = 1; i < rows.length; i++) {
        var username = rows[i].getElementsByTagName('td')[1].innerText.toLowerCase();
        var uid = rows[i].getElementsByTagName('td')[0].innerText.toLowerCase();
        if (username.includes(searchTerm) || uid.includes(searchTerm)) {
            rows[i].style.display = '';
        } else {
            rows[i].style.display = 'none';
        }
    }
});

// Add User Button - Show the modal
addUserButton.addEventListener('click', function() {
    addUserModal.style.display = 'block';
});

// Close the Add User Modal
closeModal.addEventListener('click', function() {
    addUserModal.style.display = 'none';
});

// Save User Button - Add a new user to Firestore
saveUserButton.addEventListener('click', function() {
    var username = newUsername.value;
    var email = newEmail.value;
    if (username && email) {
        db.collection('users').add({
            uid: 'YOUR_UID', // Set the UID here
            username: username,
            email: email
        });
        newUsername.value = '';
        newEmail.value = '';
        addUserModal.style.display = 'none';
    } else {
        alert('Please enter both a username and email.');
    }
});

// Edit and Delete buttons
userTable.addEventListener('click', function(e) {
    if (e.target.classList.contains('delete')) {
        var id = e.target.parentElement.parentElement.getAttribute('data-id');
        db.collection('users').doc(id).delete();
    } else if (e.target.classList.contains('edit')) {
        // Handle editing here
    }
});
