import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUserData } from 'app/shared/model/user-data.model';
import { getEntities } from './user-data.reducer';

export const UserData = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const userDataList = useAppSelector(state => state.userData.entities);
  const loading = useAppSelector(state => state.userData.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="user-data-heading" data-cy="UserDataHeading">
        User Data
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh List
          </Button>
          <Link to="/user-data/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create new User Data
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {userDataList && userDataList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>ID</th>
                <th>Rf Id</th>
                <th>Restricted</th>
                <th>Count</th>
                <th>Updated At</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {userDataList.map((userData, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/user-data/${userData.id}`} color="link" size="sm">
                      {userData.id}
                    </Button>
                  </td>
                  <td>{userData.rfId}</td>
                  <td>{userData.restricted ? 'true' : 'false'}</td>
                  <td>{userData.count}</td>
                  <td>{userData.updatedAt ? <TextFormat type="date" value={userData.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/user-data/${userData.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`/user-data/${userData.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button tag={Link} to={`/user-data/${userData.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No User Data found</div>
        )}
      </div>
    </div>
  );
};

export default UserData;
