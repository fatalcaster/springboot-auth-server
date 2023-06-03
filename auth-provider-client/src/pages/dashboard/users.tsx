import DashboardPageLayout, {
  MenuItem,
} from "@/components/DashboardPageLayout";
import useUserManager from "@/hooks/useUserManager";
import { authApi } from "@/ServerAPI/AuthAPI";
import { userApi } from "@/ServerAPI/UserAPI";
import { User } from "@/types/User";
import {
  ContentBox,
  SmallButton,
  Table,
  TableRow,
} from "codedepo-themed-components-nextjs";
import { GetServerSideProps } from "next";
import Image from "next/image";

export default function UserDashboardPage({ users }: { users: User[] }) {
  const { userList } = useUserManager({ initialUsers: users });
  console.log(userList);
  return (
    <DashboardPageLayout currentPage={MenuItem.Users}>
      <div className="w-full table border-spacing-y-2">
        <ContentBox className="table-header-group border-spacing-2 ">
          <div className="table-cell px-2 ">User</div>
          <div className="table-cell">Roles</div>
        </ContentBox>
        {users.map((user, index) => (
          <ContentBox key={user.id} className=" h-12 table-row middle">
            <div className="table-cell">
              <div className="flex items-center p-1">
                <Image
                  src="/logo.svg"
                  width={25}
                  height={25}
                  alt="profile"
                  className="w-12 h-12 bg-secondary-800 rounded-full border-2 mr-2 border-primary"
                />
                <span>{user.email}</span>
              </div>
            </div>
            <div className="table-cell align-middle">
              {user.roles.map((role, index) => (
                <span
                  className="bg-secondary-800 text-primary mx-1 py-1 px-2"
                  key={index}
                >
                  {role.split("_")[1].toLowerCase()}
                </span>
              ))}
            </div>
            <div className="table-cell align-middle">test</div>
          </ContentBox>
        ))}
      </div>
    </DashboardPageLayout>
  );
}
function CardBackside() {}
export const getServerSideProps: GetServerSideProps<{
  users: User[];
  error: any;
  accessToken: string;
}> = async (context) => {
  const response = await authApi.refreshToken(
    context.req.cookies.refresh_token
  );
  let users: User[] = [];
  if (response.response)
    users = (await userApi.getAllUsers(0, response.response)).response || [];
  // console.log(users);
  return {
    props: {
      users: users,
      error: "",
      accessToken: response.response!,
    },
  };
};
