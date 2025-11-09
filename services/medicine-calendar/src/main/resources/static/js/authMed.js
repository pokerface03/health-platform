var keycloak;

const initKeycloak = async () => {
  keycloak = new Keycloak({
    url: window.location.origin +'/auth',
    realm: 'HealthPlatform',
    clientId: 'medicine'
  });

  await keycloak.init({
    onLoad: 'check-sso',
    silentCheckSsoRedirectUri: `${window.location}/silent-check-sso.html`
  }).then((authenticated) => {
    if (authenticated) {
      storeToken();
      displayContainer();
    } else {
      keycloak.login();
    }
  }).catch(() => {
    console.error("Failed to initialize Keycloak");
  });

  keycloak.onTokenExpired = () => {
    keycloak.updateToken(-1);
  }

  keycloak.onAuthLogout = () =>{
    logout();
  }

  // se function
  const event = new CustomEvent("keycloakInitialized", {
    detail: { authenticated: keycloak.authenticated, token: keycloak.token }
  });
  document.dispatchEvent(event); 
}

const storeToken = () => {
  localStorage.setItem("keycloakToken", keycloak.token);
}

const removeToken = () => {
  localStorage.removeItem("keycloakToken");
}

const displayContainer = () => {
  document.getElementById('container').hidden=false;
  document.getElementById('top').style.display = "flex";
}

const logout = () => {
  removeToken();
  if (keycloak) {
    keycloak.logout({ redirectUri: window.location });
  }
}

initKeycloak();