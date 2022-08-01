import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import UserData from './user-data';
import UserDataDetail from './user-data-detail';
import UserDataUpdate from './user-data-update';
import UserDataDeleteDialog from './user-data-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={UserDataUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={UserDataUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={UserDataDetail} />
      <ErrorBoundaryRoute path={match.url} component={UserData} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={UserDataDeleteDialog} />
  </>
);

export default Routes;
