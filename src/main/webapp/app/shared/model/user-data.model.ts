import dayjs from 'dayjs';

export interface IUserData {
  id?: number;
  rfId?: string | null;
  restricted?: boolean | null;
  count?: number | null;
  updatedAt?: string | null;
}

export const defaultValue: Readonly<IUserData> = {
  restricted: false,
};
