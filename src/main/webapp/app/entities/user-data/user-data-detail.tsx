import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-data.reducer';

export const UserDataDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const userDataEntity = useAppSelector(state => state.userData.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userDataDetailsHeading">UserData</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{userDataEntity.id}</dd>
          <dt>
            <span id="rfId">Rf Id</span>
          </dt>
          <dd>{userDataEntity.rfId}</dd>
          <dt>
            <span id="restricted">Restricted</span>
          </dt>
          <dd>{userDataEntity.restricted ? 'true' : 'false'}</dd>
          <dt>
            <span id="count">Count</span>
          </dt>
          <dd>{userDataEntity.count}</dd>
          <dt>
            <span id="updatedAt">Updated At</span>
          </dt>
          <dd>{userDataEntity.updatedAt ? <TextFormat value={userDataEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/user-data" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-data/${userDataEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserDataDetail;
