var keycloak = new Keycloak({
    url: 'http://localhost:8080/',
    realm: 'HealthPlatform',
    clientId: 'vitals'
  });

  keycloak.init({
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`
  }).then((authenticated) => {
    if (authenticated) {
      console.log("Authenticated!", keycloak.token);
      console.log(keycloak.loadUserInfo());
      localStorage.setItem("keycloakToken", keycloak.token);
      scheduleTokenRefresh();

    } else {
      console.log("Not Authenticated!")
      keycloak.login();
    }
  }).catch(() => {
    console.error("Failed to initialize Keycloak");
  });

  function scheduleTokenRefresh() {
    // Try to refresh the token every 30 seconds
    setInterval(() => {
      keycloak.updateToken(30).then(refreshed => {
        if (refreshed) {
          console.log("🔄 Token refreshed");
        } else {
          console.log("Token still valid");
        }
        localStorage.setItem("keycloakToken", keycloak.token);
      }).catch(() => {
        console.error("Failed to refresh token, logging out...");
        keycloak.logout();
      });
    }, 30000);
  }


  const logout = () => {
    keycloak.logout({ redirectUri: window.location.href });
  }