package com.onlinebookstore.common.security;

import com.onlinebookstore.auth.bean.AuthBean;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

/**
 * JSF PhaseListener security guard for administrative view lifecycle restoration.
 * Ensures JSF phase processing halts immediately for unauthorized customer requests.
 */
public class AdminSecurityPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        // No-op before RESTORE_VIEW
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();
        if (context == null || context.getViewRoot() == null) {
            return;
        }

        String viewId = context.getViewRoot().getViewId();
        if (viewId != null && viewId.startsWith("/pages/admin/")) {
            AuthBean authBean = null;
            try {
                authBean = CDI.current().select(AuthBean.class).get();
            } catch (Exception ignored) {
                authBean = context.getApplication().evaluateExpressionGet(context, "#{authBean}", AuthBean.class);
            }

            boolean loggedIn = authBean != null && authBean.isLoggedIn();
            boolean hasAdminAccess = authBean != null && (authBean.isAdmin() || authBean.isManager());

            NavigationHandler navHandler = context.getApplication().getNavigationHandler();

            if (!loggedIn) {
                navHandler.handleNavigation(context, null, "/pages/auth/login.xhtml?faces-redirect=true");
                context.renderResponse();
            } else if (!hasAdminAccess) {
                navHandler.handleNavigation(context, null, "/pages/error/403.xhtml?faces-redirect=true");
                context.renderResponse();
            }
        }
    }
}
