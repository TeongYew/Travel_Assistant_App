const userList = document.querySelector('.user');

// setup guides
const setupUser = (data) => {

  let html = '';
  data.forEach(doc => {
    const user = doc.data();
    const li = `
      <li>
        <div class="collapsible-header grey lighten-4"> ${user.user_name} </div>
        <div class="collapsible-body white"> ${user.user_email} </div>
      </li>
    `;
    html += li;
  });
  userList.innerHTML = html

};

// setup materialize components
document.addEventListener('DOMContentLoaded', function() {

    var modals = document.querySelectorAll('.modal');
    M.Modal.init(modals);
  
    var items = document.querySelectorAll('.collapsible');
    M.Collapsible.init(items);
  
  });