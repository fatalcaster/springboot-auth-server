import { Link, NavBar, NavIcon } from "codedepo-themed-components-nextjs";
import Image from "next/image";
import { ReactNode, useState } from "react";
import { DocsIcon, RocketIcon, SettingIcon, UsersIcon } from "./Icons";

interface DashboardPageLayoutProps {
  children?: ReactNode;

  currentPage: MenuItem;
}

export enum MenuItem {
  Profile,
  Apps,
  Users,
  Docs,
}

const MenuItems = [
  {
    label: "Users",
    href: "/dashboard/users",
    value: MenuItem.Users,
    icon: <UsersIcon />,
  },
  { label: "Docs", href: "/docs", value: MenuItem.Docs, icon: <DocsIcon /> },
  {
    label: "My apps",
    href: "/dashboard/apps",
    value: MenuItem.Apps,
    icon: <RocketIcon />,
  },
  {
    label: "Settings",
    href: "/dashboard",
    value: MenuItem.Profile,
    icon: <SettingIcon />,
  },
];

export default function DashboardPageLayout({
  children,
  currentPage,
}: DashboardPageLayoutProps) {
  const [navVisible, setNavVisible] = useState(false);

  const toggleNav = () => {
    if (navVisible) {
      setMenuInvisible();
    } else setMenuVisible();
  };
  const setMenuVisible = () => {
    document.body.style.overflow = "hidden";
    setNavVisible(true);
  };

  const setMenuInvisible = () => {
    document.body.style.overflow = "auto";
    setNavVisible(false);
  };
  return (
    <>
      {navVisible && (
        <MobileNavMenu
          currentPage={currentPage}
          closeMenu={() => setNavVisible(false)}
        />
      )}

      <NavBar>
        <div className="max-w-7xl w-full flex items-center">
          <Image
            alt="Codedepo"
            src="/logo.svg"
            width={200}
            height={100}
            className="max-h-10"
          />
          <button onClick={toggleNav} className="ml-auto mr-4 lg:hidden">
            <NavIcon className="w-12" />
          </button>
        </div>
        {/* <LargeInput
          placeholder="Search..."
          className="w-96"
          //   style={{ width: "500px" }}
        /> */}
        {/* <div>test</div> */}
      </NavBar>
      <main className="p-4 pt-12  flex flex-col items-center">
        <div className="max-w-7xl w-full lg:flex">
          <div className="w-full">{children}</div>
          <Menu className="ml-auto hidden lg:block" currentPage={currentPage} />
        </div>
      </main>
    </>
  );
}

function MobileNavMenu({
  currentPage,
  closeMenu,
}: {
  currentPage: MenuItem;
  closeMenu: () => void;
}) {
  return (
    <div className="relative">
      <div
        className="lg:hidden bg-transparent-dark fixed w-screen h-screen"
        onClick={closeMenu}
      >
        <Menu currentPage={currentPage} />
      </div>
    </div>
  );
}
function Menu({
  currentPage,
  className = "",
}: {
  currentPage: MenuItem;
  className?: string;
}) {
  return (
    <div
      className={`absolute  lg:relative z-10 w-8/12 max-w-sm h-screen right-0 md:p-6 pt-16 lg:pt-0 shadow-neutral-700 bg-secondary-800 shadow-2xl lg:shadow-none ${className}`}
    >
      <ul>
        {MenuItems.map((item, index) => {
          if (item.value === currentPage)
            return (
              <li
                className="my-2 bg-secondary-700 p-2 shadow-xl shadow-neutral-900 "
                key={index}
              >
                <Link className="text-secondary-300 " href={item.href}>
                  <>
                    {item.icon}
                    <span className="ml-3">{item.label}</span>
                  </>
                </Link>
                {/* </ContentBox> */}
              </li>
            );
          return (
            <li className="my-4 ml-2" key={index}>
              <Link
                className="text-secondary-300 hover:underline"
                href={item.href}
              >
                <>
                  {item.icon}
                  <span className="ml-3">{item.label}</span>
                </>
              </Link>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
