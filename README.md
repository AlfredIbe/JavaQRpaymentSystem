<script>
    document.addEventListener('DOMContentLoaded', function() {
      // Fetch user data from sessionStorage
      const buyerData = sessionStorage.getItem('buyer');

      
      if (buyerData) {
        // Parse the buyer object
        const buyer = JSON.parse(buyerData);

        // Update the HTML elements with user details
        document.getElementById('userName').textContent = buyer.username; // Replace 'name' with the actual property name
        document.getElementById('userEmail').textContent = "not available"; // Replace 'email' with the actual property name
    } else {
        console.error('No buyer data found in sessionStorage.');
         // Optionally, redirect to the login page
         // window.location.href = "/login";
    }
});
    
</script>
